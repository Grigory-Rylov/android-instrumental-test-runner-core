package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.XmlReportGeneratorDelegate;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.EmptyTestRunListener;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.NodeType;
import com.github.grishberg.tests.planner.TestPlanElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

/**
 * Executes instrumentation test for single test method.
 */
public class SingleInstrumentalTestCommand implements DeviceRunnerCommand {
    private static final String TAG = "SITestCommand";

    private static final String CLASS = "class";
    private static final String PACKAGE = "package";
    private final String projectName;
    private String testName;
    private final Map<String, String> providedInstrumentationArgs;
    private final Map<String, String> instrumentationArgs;
    private final List<TestPlanElement> allPlannedTests;
    private final XmlReportGeneratorDelegate xmlReportGeneratorDelegate;
    private final RetryHandler retryHandler;

    public SingleInstrumentalTestCommand(String projectName,
                                         String testReportSuffix,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution) {
        this(projectName, testReportSuffix, instrumentalArgs, testForExecution,
                XmlReportGeneratorDelegate.STUB.INSTANCE,
                RetryHandler.NOOP);
    }

    public SingleInstrumentalTestCommand(String projectName,
                                         String testReportSuffix,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution,
                                         RetryHandler retryHandler) {
        this(projectName, testReportSuffix, instrumentalArgs, testForExecution,
                XmlReportGeneratorDelegate.STUB.INSTANCE, retryHandler);
    }

    public SingleInstrumentalTestCommand(String projectName,
                                         String testReportSuffix,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution,
                                         XmlReportGeneratorDelegate xmlReportGeneratorDelegate,
                                         RetryHandler retryHandler) {
        this.projectName = projectName;
        this.testName = testReportSuffix;
        this.providedInstrumentationArgs = instrumentalArgs;
        this.xmlReportGeneratorDelegate = xmlReportGeneratorDelegate;
        this.allPlannedTests = flattenTests(testForExecution);
        this.instrumentationArgs = new HashMap<>(providedInstrumentationArgs);
        this.retryHandler = retryHandler;

        initTestArgs(testForExecution);
    }

    private static List<TestPlanElement> flattenTests(List<TestPlanElement> testForExecution) {
        return testForExecution.stream()
                .flatMap(plan -> plan.getType() == NodeType.METHOD ?
                        Stream.of(plan) : plan.getAllTestMethods().stream())
                .peek(plan -> {
                    assert plan.getType() == NodeType.METHOD;
                })
                .collect(Collectors.toList());
    }

    private void initTestArgs(List<TestPlanElement> testsForExecution) {
        if (testsForExecution.isEmpty()) {
            throw new IllegalArgumentException("Tests plans list must not be empty");
        }
        StringBuilder sbClass = new StringBuilder();
        StringBuilder sbPackage = new StringBuilder();
        for (TestPlanElement plan : testsForExecution) {
            if (plan.isPackage()) {
                if (sbPackage.length() > 0) {
                    sbPackage.append(",");
                }
                sbPackage.append(plan.getAmInstrumentCommand());
                continue;
            }

            if (sbClass.length() > 0) {
                sbClass.append(",");
            }
            sbClass.append(plan.getAmInstrumentCommand());
        }

        if (sbClass.length() > 0) {
            instrumentationArgs.put(CLASS, sbClass.toString());
        }
        if (sbPackage.length() > 0) {
            instrumentationArgs.put(PACKAGE, sbPackage.toString());
        }
    }

    private TestsCommandResult executeImpl(ConnectedDeviceWrapper targetDevice,
                                           TestRunnerContext context,
                                           RunnerLogger logger) throws CommandExecutionException {
        InstrumentalExtension instrumentationInfo = context.getInstrumentalInfo();
        Environment environment = context.getEnvironment();

        TestRunnerBuilder testRunnerBuilder = context.createTestRunnerBuilder(projectName,
                testName,
                instrumentationArgs,
                targetDevice,
                xmlReportGeneratorDelegate);

        String singleTestMethodPrefix = String.format("%s#%s", targetDevice.getName(), testName);
        TestXmlReportsGenerator testRunListener = testRunnerBuilder.getTestRunListener();

        TestTracker testTracker = new TestTracker(logger);
        ProcessCrashedException processCrashedException = null;
        try {
            testRunnerBuilder.getTestRunner().run(testRunListener, testTracker);

            assert testRunListener.getRunResult().getNumAllFailedTests()
                    == testTracker.failedTests.size() :
                    "Tests run listener found " +
                            testRunListener.getRunResult().getNumAllFailedTests() +
                            " failed tests, but test tracker found " +
                            testTracker.failedTests.size();

            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        singleTestMethodPrefix,
                        testRunnerBuilder.getCoverageFile(),
                        environment.getCoverageDir(),
                        logger);
            }
        } catch (ProcessCrashedException e) {
            processCrashedException = e;
            TestIdentifier currentTest = testRunListener.getCurrentTest();
            String failMessage = context.getProcessCrashedHandler()
                    .provideFailMessageOnProcessCrashed(targetDevice, currentTest);
            testRunListener.failLastTest(failMessage);
            testTracker.failLastTest(failMessage);
            testRunListener.testRunEnded(0, new HashMap<>());
        } catch (Throwable e) {
            throw new CommandExecutionException("SingleInstrumentalTestCommand.execute failed:", e);
        }

        return new TestsCommandResult(testTracker.failedTests, processCrashedException);
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice, TestRunnerContext context)
            throws CommandExecutionException {
        RunnerLogger logger = context.getLogger();

        Queue<SingleInstrumentalTestCommand> commands = new ArrayDeque<>();
        commands.add(this);

        DeviceCommandResult result = new DeviceCommandResult();
        int counter = 0;
        List<TestPlanElement> failedTests = new ArrayList<>();
        List<TestPlanElement> testsLeft = Collections.EMPTY_LIST;
        while (!commands.isEmpty()) {
            SingleInstrumentalTestCommand command = commands.poll();
            testsLeft = command.allPlannedTests;
            int lastSize = testsLeft.size();
            TestsCommandResult testsResult = command.executeImpl(targetDevice, context, logger);
            failedTests.addAll(testsResult.failedTests);
            if (!testsResult.failedTests.isEmpty()) {
                result.setFailed(true);
            }
            if (testsResult.processCrashedException != null) {
                logger.e(TAG, "Process crashed", testsResult.processCrashedException);

                context.getProcessCrashedHandler().onAfterProcessCrashed(targetDevice, context);

                String newTestName = String.format("%s@%03d", testName, counter++);
                if (!testsLeft.isEmpty()) {
                    logger.i(TAG, "{} tests left after crash, enqueuing 'left-over' command.",
                            testsLeft.size());
                    assert lastSize > testsLeft.size() :
                            "Last size is " + lastSize + ", but left tests are " + testsLeft.size();

                    commands.add(new SingleInstrumentalTestCommand(projectName, newTestName,
                            providedInstrumentationArgs, testsLeft, xmlReportGeneratorDelegate,
                            // They aren't supposed to be called. Let's protect them.
                            RetryHandler.FAIL_ON_CALL));
                }
            }
        }

        if (!testsLeft.isEmpty()) {
            logger.w(TAG, "Some tests left unrun: {}", testsLeft);
        }

        if (result.isFailed()) {
            assert !failedTests.isEmpty() : "Tests run failed, but no failed tests found";
        } else {
            assert failedTests.isEmpty() : "Tests run was successful, but failed tests found";
        }

        if (result.isFailed()) {
            logger.i(TAG, "{} tests failed on device = {} during command = {}. " +
                            "Will attempt to rerun them", failedTests.size());
            retryFailedTests(targetDevice, context, failedTests, result);
        }

        return result;
    }

    private void retryFailedTests(
            ConnectedDeviceWrapper targetDevice, TestRunnerContext context,
            List<TestPlanElement> failedTests,
            DeviceCommandResult result)
            throws CommandExecutionException {
        RunnerLogger logger = context.getLogger();
        List<DeviceRunnerCommand> commands = retryHandler.getRetryCommands(
                failedTests, providedInstrumentationArgs);
        if (commands.isEmpty()) {
            logger.i(TAG, "[{}] Tests rerun attempt was stopped, " +
                    "because no retry commands were provided", targetDevice);
            return;
        }

        DeviceCommandResult retryResult = new DeviceCommandResult();
        for (DeviceRunnerCommand command : commands) {
            String commandString = command.toString();
            logger.i(TAG, "[{}] Before executing retry-command = {}",
                    targetDevice, commandString);
            if (command.execute(targetDevice, context).isFailed()) {
                retryResult.setFailed(true);
            }
            logger.i(TAG, "[{}] After executing retry-command = {}",
                    targetDevice, commandString);
        }

        if (retryResult.isFailed()) {
            logger.i(TAG, "[{}] Retry failed, there still were failed tests",
                    targetDevice);
        } else {
            result.setFailed(false);
            logger.i(TAG, "[{}] Retry was successful, all tests finished correctly",
                    targetDevice);
        }
    }

    @Override
    public String toString() {
        return "SingleInstrumentalTestCommand{ " + instrumentationArgs + " }";
    }

    private class TestTracker extends EmptyTestRunListener {
        private final RunnerLogger logger;
        final List<TestPlanElement> failedTests = new ArrayList<>();
        @CheckForNull
        private TestIdentifier currentTest;
        private boolean isCurrentTestFailed;

        TestTracker(RunnerLogger logger) {
            this.logger = logger;
        }

        @Override
        public void testStarted(@CheckForNull TestIdentifier test) {
            isCurrentTestFailed = false;
            currentTest = test;
        }

        @Override
        public void testEnded(@Nullable TestIdentifier test,
                              @Nullable Map<String, String> testMetrics) {
            currentTest = null;
            if (test == null) {
                return;
            }
            String methodName = getTestMethodName(test);
            boolean removed = allPlannedTests.removeIf(
                    plan -> isPlanForTest(plan, test, methodName));
            if (!removed) {
                logger.w(TAG, "Test '{}' '{}' was not planned to be run but did.",
                        test.getClassName(), methodName);
            }
        }

        @Override
        public void testFailed(@Nullable TestIdentifier test, @Nullable String trace) {
            if (isCurrentTestFailed || test == null || currentTest == null) {
                return;
            }
            isCurrentTestFailed = true;

            String methodName = getTestMethodName(test);
            Stream<TestPlanElement> failedTestStream = allPlannedTests.stream()
                    .filter(plan -> isPlanForTest(plan, test, methodName));
            Optional<TestPlanElement> failedTestPlan = failedTestStream.findFirst();
            assert failedTestPlan.isPresent() :
                    "Test plan for test {" + test.getClassName() +
                            "#" + methodName + "} wasn't found";
            failedTests.add(failedTestPlan.get());
        }

        @NotNull
        private String getTestMethodName(@NotNull TestIdentifier test) {
            String methodName = test.getTestName();
            if (methodName.indexOf('[') > 0) {
                methodName = methodName.substring(0, test.getTestName().indexOf('['));
            }
            return methodName;
        }

        private boolean isPlanForTest(TestPlanElement plan,
                                      TestIdentifier test, String methodName) {
            return Objects.equals(plan.getClassName(), test.getClassName()) &&
                    Objects.equals(plan.getMethodName(), methodName);
        }

        void failLastTest(String failMessage) {
            testFailed(currentTest, failMessage);
            testEnded(currentTest, null);
        }
    }

    private class TestsCommandResult {
        final List<TestPlanElement> failedTests;
        @Nullable
        final ProcessCrashedException processCrashedException;

        TestsCommandResult(List<TestPlanElement> failedTests,
                           @Nullable ProcessCrashedException processCrashedException) {
            this.failedTests = failedTests;
            this.processCrashedException = processCrashedException;
            assert processCrashedException == null || !failedTests.isEmpty() :
                    "Process crashed, but no failed tests found";
        }
    }

    /**
     * Provides a way to retry specific tests run command in the same prepared environment
     * with additional clean up if needed.
     */
    public interface RetryHandler {
        RetryHandler NOOP = (List<TestPlanElement> failedTests,
                             Map<String, String> providedInstrumentationArgs)
                -> Collections.emptyList();
        RetryHandler FAIL_ON_CALL = (failedTests, providedInstrumentationArgs) -> {
            throw new IllegalStateException("Retry was forbidden to run");
        };

        /**
         * @return commands to run failed tests. Cleanup commands can be included. Commands will
         * be run run after failed tests found, so prepared device environment by previous commands
         * is ready.
         * <br/>If retry isn't needed, return empty list or use {@link #NOOP}
         * <br/><b>NOTE:</b> new tests commands must have different test name than previous one.
         */
        @NotNull
        List<DeviceRunnerCommand> getRetryCommands(
                List<TestPlanElement> failedTests, Map<String, String> providedInstrumentationArgs);
    }
}

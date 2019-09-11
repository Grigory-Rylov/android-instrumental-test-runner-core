package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.*;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.EmptyTestRunListener;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.NodeType;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public SingleInstrumentalTestCommand(String projectName,
                                         String testReportSuffix,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution) {
        this(projectName, testReportSuffix, instrumentalArgs, testForExecution,
                XmlReportGeneratorDelegate.STUB.INSTANCE);
    }

    public SingleInstrumentalTestCommand(String projectName,
                                         String testReportSuffix,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution,
                                         XmlReportGeneratorDelegate xmlReportGeneratorDelegate) {
        this.projectName = projectName;
        this.testName = testReportSuffix;
        this.providedInstrumentationArgs = instrumentalArgs;
        this.xmlReportGeneratorDelegate = xmlReportGeneratorDelegate;
        this.allPlannedTests = flattenTests(testForExecution);
        this.instrumentationArgs = new HashMap<>(providedInstrumentationArgs);

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

    private DeviceCommandResult executeImpl(ConnectedDeviceWrapper targetDevice,
                                            TestRunnerContext context) throws CommandExecutionException {
        DeviceCommandResult result = new DeviceCommandResult();
        InstrumentalExtension instrumentationInfo = context.getInstrumentalInfo();
        Environment environment = context.getEnvironment();

        TestRunnerBuilder testRunnerBuilder = context.createTestRunnerBuilder(projectName,
                testName,
                instrumentationArgs,
                targetDevice,
                xmlReportGeneratorDelegate);

        String singleTestMethodPrefix = String.format("%s#%s", targetDevice.getName(), testName);
        TestXmlReportsGenerator testRunListener = testRunnerBuilder.getTestRunListener();

        TestTracker testTracker = new TestTracker(context.getLogger());
        try {
            testRunnerBuilder.getTestRunner().run(testRunListener, testTracker);

            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());

            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        singleTestMethodPrefix,
                        testRunnerBuilder.getCoverageFile(),
                        environment.getCoverageDir(),
                        testRunnerBuilder.getRunTestLogger());
            }
        } catch (ProcessCrashedException e) {
            TestIdentifier currentTest = testRunListener.getCurrentTest();
            String failMessage = context.getProcessCrashedHandler()
                    .provideFailMessageOnProcessCrashed(targetDevice, currentTest);
            testRunListener.failLastTest(failMessage);
            testTracker.endLastTest();
            testRunListener.testRunEnded(0, new HashMap<>());

            throw e;
        } catch (Exception e) {
            throw new CommandExecutionException("SingleInstrumentalTestCommand.execute failed:", e);
        }
        return result;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice, TestRunnerContext context)
            throws CommandExecutionException {
        RunnerLogger logger = context.getLogger();

        Queue<SingleInstrumentalTestCommand> commands = new ArrayDeque<>();
        commands.add(this);

        DeviceCommandResult result = new DeviceCommandResult();
        int counter = 0;
        List<TestPlanElement> testsLeft = Collections.EMPTY_LIST;
        while (!commands.isEmpty()) {
            SingleInstrumentalTestCommand command = commands.poll();
            testsLeft = command.allPlannedTests;
            int lastSize = testsLeft.size();
            try {
                if (command.executeImpl(targetDevice, context).isFailed()) {
                    result.setFailed(true);
                }
            } catch (ProcessCrashedException e) {
                result.setFailed(true);
                logger.e(TAG, "Process crashed", e);
                String newTestName = String.format("%s@%03d", testName, counter++);
                if (!testsLeft.isEmpty()) {
                    logger.i(TAG, "{} tests left after crash, enqueuing 'left-over' command.",
                            testsLeft.size());
                    assert lastSize > testsLeft.size();
                    commands.add(new SingleInstrumentalTestCommand(projectName, newTestName,
                            providedInstrumentationArgs, testsLeft));
                }
            }
        }
        if (!testsLeft.isEmpty()) {
            logger.w(TAG, "Some tests left unrun: {}", testsLeft);
        }
        return result;
    }

    @Override
    public String toString() {
        return "SingleInstrumentalTestCommand{ " + instrumentationArgs + " }";
    }

    private class TestTracker extends EmptyTestRunListener {
        private final RunnerLogger logger;
        @CheckForNull
        private TestIdentifier currentTest;

        TestTracker(RunnerLogger logger) {
            this.logger = logger;
        }

        @Override
        public void testStarted(@CheckForNull TestIdentifier test) {
            currentTest = test;
        }

        @Override
        public void testEnded(@Nullable TestIdentifier test,
                              @Nullable Map<String, String> testMetrics) {
            currentTest = null;
            if (test == null) {
                return;
            }
            String methodName = test.getTestName().substring(0, test.getTestName().indexOf('['));
            boolean removed = allPlannedTests.removeIf(plan ->
                    Objects.equals(plan.getClassName(), test.getClassName()) &&
                    Objects.equals(plan.getMethodName(), methodName));
            if (!removed) {
                logger.w(TAG, "Test '{}' '{}' was not planned to be run but did.",
                        test.getClassName(), methodName);
            }
        }

        void endLastTest() {
            testEnded(currentTest, null);
        }
    }
}

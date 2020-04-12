package com.github.grishberg.tests.commands;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.ddmlib.testrunner.TestRunResult;
import com.android.utils.ILogger;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.ProcessCrashHandler;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand.RetryHandler;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.TestPlanElement;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleInstrumentalTestCommandRetryTest {
    private static final String PROJECT_NAME = "test_project";
    private static final String TEST_CLASS = "com.test.TestClass";
    private static final String TEST_NAME_PREFIX = "test";
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    IDevice device;
    @Mock
    Environment environment;
    @Mock
    RunnerLogger logger;
    @Mock
    TestRunnerContext context;
    @Mock
    TestRunnerBuilder testRunnerBuilder;
    @Mock
    TestXmlReportsGenerator reportsGenerator;
    @Mock
    RemoteAndroidTestRunner testRunner;
    @Mock
    TestRunResult testRunResult;
    @Mock
    ILogger iLogger;
    @Mock
    File coverageDir;
    @Mock
    ProcessCrashHandler processCrashedHandler;

    private HashMap<String, String> args = new HashMap<>();
    private InstrumentalExtension ext = new InstrumentalExtension();
    private Map<String, String> instrumentationArgs;
    private TestIdentifier currentTest = new TestIdentifier(TEST_CLASS, TEST_NAME_PREFIX);

    private Queue<TestPlanElement> testElements = new ArrayDeque<>();
    private Set<String> testsToFail = new HashSet<>();
    private Set<String> testsToCrash = new HashSet<>();
    private Set<String> failedTests = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        when(processCrashedHandler.provideFailMessageOnProcessCrashed(any(), any()))
                .thenReturn("Process was crashed. See logcat to details.");
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(context.getEnvironment()).thenReturn(environment);
        when(context.getProcessCrashedHandler()).thenReturn(processCrashedHandler);
        when(deviceWrapper.getLogger()).thenReturn(mock(RunnerLogger.class));
        doAnswer((Answer<TestRunnerBuilder>) invocation -> {
            instrumentationArgs = invocation.getArgument(2);
            return testRunnerBuilder;
        }).when(context).createTestRunnerBuilder(any(), any(), any(), any(), any());

        when(testRunnerBuilder.getTestRunListener()).thenReturn(reportsGenerator);
        when(testRunnerBuilder.getTestRunner()).thenReturn(testRunner);

        when(reportsGenerator.getRunResult()).thenReturn(testRunResult);

        when(deviceWrapper.getName()).thenReturn("test_device");

        for (int i = 0; i < 10; ++i) {
            testElements.add(new TestPlanElement("", TEST_NAME_PREFIX + i, TEST_CLASS));
        }
        doAnswer(invocation -> failedTests.size()).when(testRunResult).getNumAllFailedTests();

        doAnswer(invocation -> {
            failedTests.clear();
            List<ITestRunListener> listeners
                    = Arrays.stream(invocation.getArguments())
                    .map(o -> ((ITestRunListener) o))
                    .collect(Collectors.toList());

            ImmutableList<ITestRunListener> reverseListeners
                    = ImmutableList.copyOf(listeners).reverse();

            while (!testElements.isEmpty()) {
                TestPlanElement test = testElements.poll();
                TestIdentifier testIdentifier =
                        new TestIdentifier(test.getClassName(), test.getMethodName());
                listeners.forEach(listener -> {
                    listener.testStarted(testIdentifier);
                });

                if (testsToCrash.contains(test.getMethodName())) {
                    failedTests.add(test.getMethodName());
                    throw new ProcessCrashedException(
                            "Process crashed during " + test.getMethodName());
                }

                reverseListeners.forEach(listener -> {
                    if (testsToFail.contains(test.getMethodName())) {
                        failedTests.add(test.getMethodName());
                        listener.testFailed(testIdentifier, test.getMethodName() + " failed");
                    }
                    listener.testEnded(testIdentifier, null);
                });
            }

            return null;
        }).when(testRunner).run((ITestRunListener[]) any());
    }

    @Test
    public void retryTestsOnFailureWasSuccessfulForFlakyTests() {
        failTests(0, 8);
        crashTests(1, 4);

        assertTrue(runTests(
                () -> {
                    failTests();
                    crashTests(8);
                },
                () -> {
                    // Success, all broken tests were flaky.
                    failTests();
                    crashTests();
                })
        );
    }

    @Test
    public void retryTestsOnFailureWasFailureForBrokenTests() {
        failTests(1, 4);
        crashTests(5, 9);

        Runnable attemptTuner = () -> {
            // Broken & Failed tests still remains the same on every attempt.
        };
        assertFalse(runTests(attemptTuner, attemptTuner, attemptTuner));
    }

    @Test
    public void noRetryTestsByDefault() {
        failTests(1, 4);
        crashTests(5, 9);

        assertFalse(runTests());
    }

    @Test
    public void noRetryTestsOnFailureWhenTestsBrokenButRetryHandlerReturnsNoCommands() {
        failTests(1, 4);
        crashTests(5, 9);

        assertFalse(
                runTests((failedTests, providedInstrumentationArgs) -> Collections.emptyList()));
    }

    @Test
    public void noRetryTestsOnWhenTestsAreSuccessful() {
        assertTrue(runTests((failedTests, providedInstrumentationArgs) -> {
            throw new RuntimeException("Retry must not be called for successful tests");
        }));
    }

    /**
     * @return true if tests finished successfully.
     */
    private boolean runTests(Runnable... attemptsTuners) {
        RetryHandler retryHandler = createRetryHandler(attemptsTuners);
        return runTests(retryHandler);
    }

    /**
     * @return true if tests finished successfully.
     */
    private boolean runTests(RetryHandler retryHandler) {
        try {
            SingleInstrumentalTestCommand testCommand =
                    new SingleInstrumentalTestCommand(PROJECT_NAME, "test_prefix",
                            args, new ArrayList<>(testElements), retryHandler);
            return !testCommand.execute(deviceWrapper, context).isFailed();
        } catch (CommandExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private RetryHandler createRetryHandler(Runnable... attemptsTuners) {
        if (attemptsTuners.length == 0) {
            return RetryHandler.NOOP;
        }

        return new RetryHandler() {
            private int retryIndex = 0;

            @NotNull
            @Override
            public List<DeviceRunnerCommand> getRetryCommands(
                    List<TestPlanElement> failedTests,
                    Map<String, String> providedInstrumentationArgs) {
                if (retryIndex >= attemptsTuners.length) {
                    return Collections.emptyList();
                }

                assertEquals(failedTests.size(), testsToCrash.size() + testsToFail.size());
                for (TestPlanElement test: failedTests) {
                    assertTrue(testsToFail.contains(test.getMethodName()) ||
                            testsToCrash.contains(test.getMethodName()));
                }

                testElements.clear();
                testElements.addAll(failedTests);

                attemptsTuners[retryIndex].run();
                ++retryIndex;

                assertEquals(providedInstrumentationArgs, args);
                List<DeviceRunnerCommand> commands = new ArrayList<>();
                commands.add(new SingleInstrumentalTestCommand(
                        PROJECT_NAME, "test_prefix_" + retryIndex,
                        args, new ArrayList<>(failedTests), this));
                return commands;
            }
        };
    }

    private void crashTests(int... testsIndexes) {
        testsToCrash.clear();
        for (int testIndex: testsIndexes) {
            testsToCrash.add(TEST_NAME_PREFIX + testIndex);
        }
    }

    private void failTests(int... testsIndexes) {
        testsToFail.clear();
        for (int testIndex: testsIndexes) {
            testsToFail.add(TEST_NAME_PREFIX + testIndex);
        }
    }
}

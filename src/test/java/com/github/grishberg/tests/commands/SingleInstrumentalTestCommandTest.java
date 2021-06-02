package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.ProcessCrashHandler;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.XmlReportGeneratorDelegate;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.TestPlanElement;

import com.yandex.tests.VerboseLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SingleInstrumentalTestCommand}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleInstrumentalTestCommandTest {
    private static final String PROJECT_NAME = "test_project";
    private static final String TEST_CLASS = "com.test.TestClass";
    private static final String TEST_NAME = "test1";
    private static final String TEST_NAME_2 = "test2";
    private static final String TEST_NAME_3 = "test3";
    private static final String TEST_NAME_WITH_DEVICE = "test1[phone-1]";
    private static final String TEST_NAME_2_WITH_DEVICE = "test2[phone-1]";
    private static final String TEST_NAME_3_WITH_DEVICE = "test3[phone-1]";
    private static final long MAX_TIME_TO_OUTPUT = 300;

    private static final List<TestPlanElement> ONE_TEST =
            Arrays.asList(new TestPlanElement("", TEST_NAME, TEST_CLASS));

    private static final List<TestPlanElement> TWO_TESTS =
            Arrays.asList(new TestPlanElement("", TEST_NAME, TEST_CLASS),
                    new TestPlanElement("", TEST_NAME_2, TEST_CLASS));

    private static final List<TestPlanElement> THREE_TESTS =
            Arrays.asList(new TestPlanElement("", TEST_NAME, TEST_CLASS),
                    new TestPlanElement("", TEST_NAME_2, TEST_CLASS),
                    new TestPlanElement("", TEST_NAME_3, TEST_CLASS));

    private static final Answer TEST_RUN_CRASH = invocation -> {
        for (Object listener : invocation.getArguments()) {
            ((ITestRunListener) listener).testStarted(
                    new TestIdentifier(TEST_CLASS, TEST_NAME_WITH_DEVICE));
        }
        throw new ProcessCrashedException("Process crashed");
    };

    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    Environment environment;
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
    File coverageDir;

    @Mock
    ProcessCrashHandler processCrashedHandler;

    RunnerLogger logger = spy(new VerboseLogger());

    private HashMap<String, String> args = new HashMap<>();
    private InstrumentalExtension ext = new InstrumentalExtension();

    private Map<String, String> capturedInstrumentationArgs;
    private TestIdentifier currentTest = new TestIdentifier(TEST_CLASS, TEST_NAME);

    @Before
    public void setUp() {
        when(processCrashedHandler.provideFailMessageOnProcessCrashed(any(), any()))
                .thenReturn("Process was crashed. See logcat to details.");
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(context.getEnvironment()).thenReturn(environment);
        when(context.getProcessCrashedHandler()).thenReturn(processCrashedHandler);
        when(deviceWrapper.getLogger()).thenReturn(logger);
        when(environment.getCoverageDir()).thenReturn(coverageDir);
        doAnswer((Answer<TestRunnerBuilder>) invocation -> {
            capturedInstrumentationArgs = invocation.getArgument(3);
            return testRunnerBuilder;
        }).when(context).createTestRunnerBuilder(any(), any(), any(), any(), any(), any());

        when(testRunnerBuilder.getTestRunListener()).thenReturn(reportsGenerator);
        when(testRunnerBuilder.getTestRunner()).thenReturn(testRunner);
        when(testRunnerBuilder.getCoverageFile()).thenReturn("coverage_file");

        when(reportsGenerator.getRunResult()).thenReturn(testRunResult);
        when(reportsGenerator.getCurrentTest()).thenReturn(currentTest);

        when(testRunResult.getNumAllFailedTests()).thenReturn(0);

        when(deviceWrapper.getName()).thenReturn("test_device");

        ext.setApplicationId("application_id");
        ext.setMaxTimeToOutputResponseInSeconds(MAX_TIME_TO_OUTPUT);
    }

    @Test
    public void initWithClass() throws Exception {
        runCommand(ONE_TEST);

        assertEquals("com.test.TestClass#test1", capturedInstrumentationArgs.get("class"));
    }

    @Test
    public void setMaxTimeToOutputFromInstrumentalExtension() throws Exception {
        runCommand(ONE_TEST);

        verify(testRunner).setMaxTimeToOutputResponse(MAX_TIME_TO_OUTPUT, TimeUnit.SECONDS);
    }

    @Test
    public void testWhenCoverageEnabled() throws Exception {
        ext.setCoverageEnabled(true);

        runCommand(ONE_TEST);

        verify(deviceWrapper).pullCoverageFile(ext,
                "test_device#test_prefix",
                "coverage_file",
                coverageDir);
    }

    @Test
    public void pullCoverageFileWhenEnabledCoverage() throws Exception {
        ext.setCoverageEnabled(true);
        when(environment.getCoverageDir()).thenReturn(new File("/coverage"));

        runCommand(ONE_TEST);

        verify(deviceWrapper).pullCoverageFile(
                any(InstrumentalExtension.class),
                anyString(),
                anyString(),
                any(File.class));
    }

    @Test(expected = CommandExecutionException.class)
    public void throwExecuteCommandExceptionWhenSomeDeviceException() throws Exception {
        Mockito.doThrow(new IOException(new Throwable())).when(testRunner)
                .run((ITestRunListener[]) any());

        runCommand(ONE_TEST);
    }

    @Test
    public void handleProcessCrashed() throws Exception {
        withTestCrashed();

        DeviceCommandResult result = runCommand(ONE_TEST);

        assertTrue(result.isFailed());

        verify(reportsGenerator).failLastTest("Process was crashed. See logcat to details.");
        verify(reportsGenerator).testRunEnded(anyLong(), any());
        verify(processCrashedHandler).provideFailMessageOnProcessCrashed(deviceWrapper,
                currentTest);
        verify(processCrashedHandler).onAfterProcessCrashed(any(), any());
    }

    @Test
    public void handleProcessCrashedWhenProcessCrashedEarly() throws Exception {
        withTestCrashedEarly();

        DeviceCommandResult result = runCommand(ONE_TEST);

        assertTrue(result.isFailed());

        verify(reportsGenerator).failLastTest("Process was crashed. See logcat to details.");
        verify(reportsGenerator).testRunEnded(anyLong(), any());
        verify(processCrashedHandler).provideFailMessageOnProcessCrashed(deviceWrapper,
                currentTest);
        verify(processCrashedHandler).onAfterProcessCrashed(any(), any());
    }

    @Test
    public void okTestAfterProcessCrashed() throws Exception {
        with1TestCrashedAnd1TestOk();

        DeviceCommandResult result = runCommand(TWO_TESTS);

        assertTrue(result.isFailed());

        verify(testRunner, times(2)).run((ITestRunListener[]) any());
        verify(reportsGenerator, times(2)).testRunEnded(anyLong(), any());

        verify(reportsGenerator, times(1))
                .failLastTest("Process was crashed. See logcat to details.");
        verify(processCrashedHandler, times(1)).provideFailMessageOnProcessCrashed(deviceWrapper,
                currentTest);
        verify(processCrashedHandler, times(1)).onAfterProcessCrashed(any(), any());
    }

    @Test
    public void continueRunIfAllCrashes() throws Exception {
        with3TestsCrashed();

        DeviceCommandResult result = runCommand(THREE_TESTS);

        assertTrue(result.isFailed());

        verify(testRunner, times(3)).run((ITestRunListener[]) any());
        verify(reportsGenerator, times(3))
                .failLastTest("Process was crashed. See logcat to details.");
        verify(reportsGenerator, times(3)).testRunEnded(anyLong(), any());
        verify(processCrashedHandler, times(3)).provideFailMessageOnProcessCrashed(deviceWrapper,
                currentTest);
        verify(processCrashedHandler, times(3)).onAfterProcessCrashed(any(), any());

        ArgumentCaptor<Map<String, String>> instrumentationArgsCaptor =
                ArgumentCaptor.forClass(Map.class);
        verify(context, times(3)).createTestRunnerBuilder(anyString(), anyString(),
                any(TestIdentifier.class), instrumentationArgsCaptor.capture(),
                any(ConnectedDeviceWrapper.class), any(XmlReportGeneratorDelegate.class));

        List<Map<String, String>> instrumentationArgs =
                instrumentationArgsCaptor.getAllValues();
        Assert.assertEquals(instrumentationArgs.get(0).get("class"),
                "com.test.TestClass#test1,com.test.TestClass#test2,com.test.TestClass#test3");
        Assert.assertEquals(instrumentationArgs.get(1).get("class"),
                "com.test.TestClass#test2,com.test.TestClass#test3");
        Assert.assertEquals(instrumentationArgs.get(2).get("class"),
                "com.test.TestClass#test3");
    }

    @Test
    public void handleProcessCrashedWhenProcessCrashedWithoutDeviceSuffix() throws Exception {
        doAnswer(invocation -> {
            for (Object listener : invocation.getArguments()) {
                ((ITestRunListener) listener).testStarted(
                        new TestIdentifier(TEST_CLASS, TEST_NAME));
            }
            throw new ProcessCrashedException("Process crashed");
        }).when(testRunner).run((ITestRunListener[]) any());

        runCommand(ONE_TEST);
        verify(processCrashedHandler).provideFailMessageOnProcessCrashed(deviceWrapper, currentTest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenGivenEmptyTestList() throws Exception {
        runCommand(Collections.emptyList());
    }

    private void withTestCrashed() throws Exception {
        doAnswer(mockTestRunCrash(TEST_CLASS, TEST_NAME_WITH_DEVICE))
                .when(testRunner).run((ITestRunListener[]) any());
    }

    private void with3TestsCrashed() throws Exception {
        doAnswer(mockTestRunCrash(TEST_CLASS, TEST_NAME_WITH_DEVICE)
        ).doAnswer(mockTestRunCrash(TEST_CLASS, TEST_NAME_2_WITH_DEVICE)
        ).doAnswer(mockTestRunCrash(TEST_CLASS, TEST_NAME_3_WITH_DEVICE)
        ).when(testRunner).run((ITestRunListener[]) any());
    }

    private void with1TestCrashedAnd1TestOk() throws Exception {
        doAnswer(mockTestRunCrash(TEST_CLASS, TEST_NAME_WITH_DEVICE)
        ).doAnswer(mockTestRunOk(TEST_CLASS, TEST_NAME_2_WITH_DEVICE)
        ).when(testRunner).run((ITestRunListener[]) any());
    }

    private Answer mockTestRunOk(String testClass, String testName) {
        // See javadoc to ITestRunListener class for correct commands sequence
        return (InvocationOnMock invocation) -> {
            // Is there a simple way to convert Object [] -> List<ITestRunListener> ?
            List<ITestRunListener> listeners =
                    Arrays.asList(invocation.getArguments()).stream()
                            .map(arg -> (ITestRunListener) arg).collect(Collectors.toList());
            listeners.forEach(listener ->
                    listener.testRunStarted("", 1));
            listeners.forEach(listener ->
                    listener.testStarted(new TestIdentifier(testClass, testName)));
            listeners.forEach(listener ->
                    listener.testEnded(new TestIdentifier(testClass, testName),
                            Collections.emptyMap()));
            listeners.forEach(listener ->
                    listener.testRunEnded(100, Collections.emptyMap()));
            return null;
        };
    }

    private Answer mockTestRunCrash(String testClass, String testName) {
        return (InvocationOnMock invocation) -> {
            for (Object listener : invocation.getArguments()) {
                ((ITestRunListener) listener).testStarted(
                        new TestIdentifier(testClass, testName));
            }
            throw new ProcessCrashedException("Process crashed");
        };
    }

    private void withTestCrashedEarly() throws Exception {
        doAnswer(invocation -> {
            // With early native crash DDMS doesn't produce testStarted() callback.
            throw new ProcessCrashedException("Process crashed");
        }).when(testRunner).run((ITestRunListener[]) any());
    }

    private DeviceCommandResult runCommand(List<TestPlanElement> tests)
            throws CommandExecutionException {
        SingleInstrumentalTestCommand testCommand =
                new SingleInstrumentalTestCommand(PROJECT_NAME, "test_prefix", args, tests);
        return testCommand.execute(deviceWrapper, context);
    }
}

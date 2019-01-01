package com.github.grishberg.tests.commands;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.android.utils.ILogger;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by grishberg on 22.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleInstrumentalTestCommandTest {
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    IDevice device;
    @Mock
    Project project;
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
    private SingleInstrumentalTestCommand testCommand;
    private HashMap<String, String> args = new HashMap<>();
    private InstrumentalPluginExtension ext = new InstrumentalPluginExtension();
    private ArrayList<TestPlanElement> testElements = new ArrayList<>();
    private Map<String, String> instrumentationArgs;

    @Before
    public void setUp() {
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(context.getEnvironment()).thenReturn(environment);
        when(environment.getCoverageDir()).thenReturn(coverageDir);
        doAnswer((Answer<TestRunnerBuilder>) invocation -> {
            instrumentationArgs = invocation.getArgument(2);
            return testRunnerBuilder;
        }).when(context).createTestRunnerBuilder(any(), any(), any(), any());

        when(testRunnerBuilder.getTestRunListener()).thenReturn(reportsGenerator);
        when(testRunnerBuilder.getTestRunner()).thenReturn(testRunner);
        when(testRunnerBuilder.getRunTestLogger()).thenReturn(iLogger);
        when(testRunnerBuilder.getCoverageFile()).thenReturn("coverage_file");

        when(reportsGenerator.getRunResult()).thenReturn(testRunResult);

        when(deviceWrapper.getName()).thenReturn("test_device");
        testCommand = new SingleInstrumentalTestCommand(project, "test_prefix", args, testElements);
    }

    @Test
    public void initWithClass() throws Exception {
        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        SingleInstrumentalTestCommand cmd = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        cmd.execute(deviceWrapper, context);

        Assert.assertEquals("com.test.TestClass#test1", instrumentationArgs.get("class"));
    }

    @Test
    public void testWhenCoverageEnabled() throws Exception {
        ext.setCoverageEnabled(true);
        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        SingleInstrumentalTestCommand cmd = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        cmd.execute(deviceWrapper, context);

        verify(deviceWrapper).pullCoverageFile(ext,
                "test_device#test_prefix",
                "coverage_file",
                coverageDir,
                iLogger);
    }

    @Test
    public void pullCoverageFileWhenEnabledCoverage() throws Exception {
        ext.setCoverageEnabled(true);
        when(environment.getCoverageDir()).thenReturn(new File("/coverage"));

        testCommand.execute(deviceWrapper, context);

        verify(deviceWrapper).pullCoverageFile(
                any(InstrumentalPluginExtension.class),
                anyString(),
                anyString(),
                any(File.class),
                any(ILogger.class));
    }

    @Test(expected = ExecuteCommandException.class)
    public void throwExecuteCommandExceptionWhenSomeDeviceException() throws Exception {
        Mockito.doThrow(new IOException(new Throwable())).when(testRunner)
                .run(reportsGenerator);
        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        testCommand = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        testCommand.execute(deviceWrapper, context);
    }

    @Test(expected = ExecuteCommandException.class)
    public void failTestWhenProcessCrashed() throws Exception {
        Mockito.doThrow(new ProcessCrashedException("Process crashed")).when(testRunner)
                .run(reportsGenerator);

        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        testCommand = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        testCommand.execute(deviceWrapper, context);
        verify(reportsGenerator).failLastTest("Process was crashed. See logcat to details.");
        verify(reportsGenerator).testRunEnded(anyInt(), any());
    }
}
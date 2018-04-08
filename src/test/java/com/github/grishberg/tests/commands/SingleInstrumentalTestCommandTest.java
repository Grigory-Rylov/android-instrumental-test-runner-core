package com.github.grishberg.tests.commands;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.testrunner.InstrumentationResultParser;
import com.android.utils.ILogger;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 22.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleInstrumentalTestCommandTest {
    private static final String TEST_COMMAND = "am instrument -w -r   -e class com.test.TestClass#test1 null/android.test.InstrumentationTestRunner";
    private static final String TEST_COVERAGE_COMMAND = "am instrument -w -r   -e coverageFile /data/data/null/coverage.ec -e class com.test.TestClass#test1 -e coverage true null/android.test.InstrumentationTestRunner";
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
    private SingleInstrumentalTestCommand testCommand;
    private HashMap<String, String> args = new HashMap<>();
    private InstrumentalPluginExtension ext = new InstrumentalPluginExtension();
    private ArrayList<TestPlanElement> testElements = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        when(context.getLogger()).thenReturn(logger);
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(context.getEnvironment()).thenReturn(environment);
        when(deviceWrapper.getName()).thenReturn("test_device");
        when(deviceWrapper.getDevice()).thenReturn(device);
        testCommand = new SingleInstrumentalTestCommand(project, "test_prefix", args, testElements);
    }

    @Test
    public void initWithClass() throws Exception {
        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        SingleInstrumentalTestCommand cmd = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        cmd.execute(deviceWrapper, context);

        verifyExecuteDeviceCommand(TEST_COMMAND);
    }

    @Test
    public void testWhenCoverageEnabled() throws Exception {
        ext.setCoverageEnabled(true);
        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        SingleInstrumentalTestCommand cmd = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        cmd.execute(deviceWrapper, context);

        verifyExecuteDeviceCommand(TEST_COVERAGE_COMMAND);
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
        Mockito.doThrow(new IOException(new Throwable())).when(device)
                .executeShellCommand(
                        eq(TEST_COMMAND),
                        any(InstrumentationResultParser.class),
                        any(Long.class),
                        any(Long.class),
                        eq(TimeUnit.MILLISECONDS));
        testElements.add(new TestPlanElement("", "test1", "com.test.TestClass"));
        testCommand = new SingleInstrumentalTestCommand(project,
                "test_prefix", args, testElements);

        testCommand.execute(deviceWrapper, context);
    }

    private void verifyExecuteDeviceCommand(String cmd) throws Exception {
        verify(device).executeShellCommand(
                eq(cmd),
                any(InstrumentationResultParser.class),
                any(Long.class),
                any(Long.class),
                eq(TimeUnit.MILLISECONDS)
        );
    }
}
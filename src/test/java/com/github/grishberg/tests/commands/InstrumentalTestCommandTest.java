package com.github.grishberg.tests.commands;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.testrunner.InstrumentationResultParser;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 26.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentalTestCommandTest {
    private static final String CMD_WITHOUT_COVERAGE = "am instrument -w -r   com.app.test/android.test.InstrumentationTestRunner";
    private static final String CMD_WITH_COVERAGE = "am instrument -w -r   -e coverageFile /data/data/test.appId/coverage.ec -e coverage true com.app.test/android.test.InstrumentationTestRunner";
    private static final String PROJECT_NAME = "test_project";

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

    private InstrumentalExtension ext = new InstrumentalExtension();
    private InstrumentalTestCommand command;
    private HashMap<String, String> argsProvider = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(context.getEnvironment()).thenReturn(environment);
        when(deviceWrapper.getName()).thenReturn("test_device");
        when(deviceWrapper.getDevice()).thenReturn(device);
        ext.setInstrumentalPackage("com.app.test");
        ext.setApplicationId("test.appId");
        command = new InstrumentalTestCommand(PROJECT_NAME, argsProvider);
    }

    @Test
    public void executeWithoutCoverage() throws Exception {
        DeviceCommandResult result = command.execute(deviceWrapper, context);

        verifyDeviceExecuteCommand(CMD_WITHOUT_COVERAGE);
    }

    @Test
    public void executeWithCoverage() throws Exception {
        ext.setCoverageEnabled(true);
        DeviceCommandResult result = command.execute(deviceWrapper, context);

        verifyDeviceExecuteCommand(CMD_WITH_COVERAGE);
    }

    private void verifyDeviceExecuteCommand(String cmd) throws Exception {
        verify(device).executeShellCommand(
                eq(cmd),
                any(InstrumentationResultParser.class),
                any(Long.class),
                any(Long.class),
                eq(TimeUnit.MILLISECONDS)
        );
    }
}
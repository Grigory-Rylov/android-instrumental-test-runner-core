package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.ExecuteCommandException;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 27.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceCommandsRunnerTest {
    @Mock
    InstrumentalTestPlanProvider planProvider;
    @Mock
    DeviceRunnerCommandProvider commandProvider;
    @Mock
    Environment environment;
    @Mock
    RunnerLogger logger;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    DeviceRunnerCommand command;
    @Mock
    DeviceCommandResult result;
    private List<DeviceRunnerCommand> commands;
    private ConnectedDeviceWrapper[] devices;
    private DeviceCommandsRunner runner;

    @Before
    public void setUp() throws Exception {
        commands = new ArrayList<>();
        commands.add(command);
        when(commandProvider.provideCommandsForDevice(deviceWrapper, planProvider, environment))
                .thenReturn(commands);
        when(command.execute(deviceWrapper)).thenReturn(result);
        devices = new ConnectedDeviceWrapper[1];
        devices[0] = deviceWrapper;
        runner = new DeviceCommandsRunner(planProvider, commandProvider, environment, logger);
    }

    @Test
    public void runCommands() throws Exception {
        Assert.assertTrue(runner.runCommands(devices));
        verify(command).execute(deviceWrapper);
    }

    @Test
    public void runCommandsReturnFalseWhenHasFailedTests() throws Exception {
        when(result.isFailed()).thenReturn(true);
        Assert.assertFalse(runner.runCommands(devices));
        verify(command).execute(deviceWrapper);
    }

    @Test
    public void logErrorWhenException() throws Exception {
        ExecuteCommandException exception = new ExecuteCommandException("Exception", new Throwable());
        when(command.execute(deviceWrapper))
                .thenThrow(exception);
        runner.runCommands(devices);
        verify(logger).e("DCR", "Execute command exception:", exception);
    }
}
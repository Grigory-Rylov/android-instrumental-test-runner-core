package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock
    TestRunnerContext context;
    private List<DeviceRunnerCommand> commands;
    private List<ConnectedDeviceWrapper> devices;
    private DeviceCommandsRunner runner;

    @Before
    public void setUp() throws Exception {
        commands = new ArrayList<>();
        commands.add(command);
        when(context.getLogger()).thenReturn(logger);
        when(context.getEnvironment()).thenReturn(environment);
        when(commandProvider.provideCommandsForDevice(eq(deviceWrapper), eq(planProvider), any()))
                .thenReturn(commands);
        when(command.execute(eq(deviceWrapper), any())).thenReturn(result);
        devices = Arrays.asList(deviceWrapper);
        runner = new DeviceCommandsRunner(planProvider, commandProvider);
    }

    @Test
    public void runCommands() throws Exception {
        Assert.assertTrue(runner.runCommands(devices, context));
        verify(command).execute(eq(deviceWrapper), any());
    }

    @Test
    public void runCommandsReturnFalseWhenHasFailedTests() throws Exception {
        when(result.isFailed()).thenReturn(true);
        Assert.assertFalse(runner.runCommands(devices, context));
        verify(command).execute(eq(deviceWrapper), any());
    }

    @Test(expected = CommandExecutionException.class)
    public void logErrorWhenException() throws Exception {
        CommandExecutionException exception = new CommandExecutionException("Exception", new Throwable());
        when(command.execute(eq(deviceWrapper), any()))
                .thenThrow(exception);
        runner.runCommands(devices, context);
        verify(logger).e("DCR", "Execute command exception:", exception);
    }

    @Test(expected = CommandExecutionException.class)
    public void throwExecuteCommandExceptionWhenOtherException() throws Exception {
        NullPointerException exception = new NullPointerException();
        when(command.execute(eq(deviceWrapper), any()))
                .thenThrow(exception);
        runner.runCommands(devices, context);
        verify(logger).e("DCR", "Execute command exception:", exception);
    }

    @Test(expected = ProcessCrashedException.class)
    public void throwProcessCrashedExceptionWhenProcessCrashed() throws Exception {
        ProcessCrashedException exception = new ProcessCrashedException("test process crashed");
        when(command.execute(eq(deviceWrapper), any()))
                .thenThrow(exception);
        runner.runCommands(devices, context);
        verify(logger).e("DCR", "Execute command exception:", exception);
    }
}
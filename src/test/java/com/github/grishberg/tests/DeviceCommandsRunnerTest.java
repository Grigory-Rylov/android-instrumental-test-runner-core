package com.github.grishberg.tests;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.common.TestingSimpleLogger;
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

/**
 * Tests for {@link DeviceCommandsRunner}.
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
        logger = spy(new TestingSimpleLogger());
        mockDeviceBehavior(deviceWrapper,"emulator-5554", logger, result);
        when(context.getEnvironment()).thenReturn(environment);
        devices = Arrays.asList(deviceWrapper);
        runner = new DeviceCommandsRunner(planProvider, commandProvider);
    }

    private void mockDeviceBehavior(ConnectedDeviceWrapper deviceWrapper,
                                    String deviceName,
                                    RunnerLogger logger,
                                    DeviceCommandResult result)
            throws CommandExecutionException {
        when(deviceWrapper.getLogger()).thenReturn(logger);
        when(deviceWrapper.getName()).thenReturn(deviceName);
        when(commandProvider.provideCommandsForDevice(deviceWrapper, planProvider, environment))
                .thenReturn(commands);
        when(command.execute(deviceWrapper, context)).thenReturn(result);
    }

    @Test
    public void runCommands() throws Exception {
        Assert.assertTrue(runner.runCommands(devices, context));
        verify(command).execute(deviceWrapper, context);
    }

    @Test
    public void runCommandsReturnFalseWhenHasFailedTests() throws Exception {
        when(result.isFailed()).thenReturn(true);
        Assert.assertFalse(runner.runCommands(devices, context));
        verify(command).execute(deviceWrapper, context);
    }

    <E, F> void validateRightExceptionRaisedAndLogged(Throwable realException,
                                                      Class<E> expectParentException,
                                                      Class<F> expectChildException,
                                                      Throwable exceptionToLog) {
        if (expectParentException.isInstance(realException) &&
                realException.getCause() != null &&
                expectChildException.isInstance(realException.getCause())) {
            verify(logger).e("DCR", "Execute command exception:", exceptionToLog);
            return;
        }
        Assert.fail(String.format("%s (with %s) expected but have %s (with %s)",
                expectParentException, expectChildException, realException,
                realException.getCause()));
    }

    @Test
    public void logErrorWhenException() throws Exception {
        CommandExecutionException exception = new CommandExecutionException("Exception",
                new Throwable());
        when(command.execute(deviceWrapper, context))
                .thenThrow(exception);
        try {
            runner.runCommands(devices, context);
        } catch (Throwable e) {
            validateRightExceptionRaisedAndLogged(e, CommandExecutionException.class,
                    CommandExecutionException.class, exception);
            return;
        }
        Assert.fail("Exception must be thrown in this test");
    }

    @Test
    public void printDeviceNameInParentException() throws Exception {
        CommandExecutionException exception = new CommandExecutionException("Exception",
                new Throwable());
        when(command.execute(deviceWrapper, context))
                .thenThrow(exception);
        try {
            runner.runCommands(devices, context);
        } catch (Throwable e) {
            if (e instanceof CommandExecutionException) {
                Assert.assertTrue(e.getMessage().contains("emulator-5554"));
                return;
            }
            Assert.fail(String.format("Wrong exception: %s", e));
            return;
        }
        Assert.fail("Exception must be thrown in this test");
    }

    @Test
    public void throwExecuteCommandExceptionWhenOtherException() throws Exception {
        NullPointerException exception = new NullPointerException();
        when(command.execute(deviceWrapper, context))
                .thenThrow(exception);
        try {
            runner.runCommands(devices, context);
        } catch (Throwable e) {
            validateRightExceptionRaisedAndLogged(e, CommandExecutionException.class,
                    NullPointerException.class, exception);
            return;
        }
        Assert.fail("Exception must be thrown in this test");
    }

    @Test
    public void throwProcessCrashedExceptionWhenProcessCrashed() throws Exception {
        ProcessCrashedException exception = new ProcessCrashedException("test process crashed");
        when(command.execute(deviceWrapper, context))
                .thenThrow(exception);
        try {
            runner.runCommands(devices, context);
        } catch (Throwable e) {
            validateRightExceptionRaisedAndLogged(e, CommandExecutionException.class,
                    ProcessCrashedException.class, exception);
            return;
        }
        Assert.fail("Exception must be thrown in this test");
    }

    @Test
    public void manyChildsFailed() throws Exception {
        ConnectedDeviceWrapper deviceWrapper2 = mock(ConnectedDeviceWrapper.class);
        RunnerLogger logger2 = spy(new TestingSimpleLogger());
        mockDeviceBehavior(deviceWrapper2, "emulator-5555", logger2,
                mock(DeviceCommandResult.class));
        devices = Arrays.asList(deviceWrapper, deviceWrapper2);

        NullPointerException exception1 = new NullPointerException("Exception 1");
        IllegalStateException exception2 = new IllegalStateException("Exception 2");

        when(command.execute(deviceWrapper, context))
                .thenThrow(exception1);
        doAnswer(invocation -> {
            // Delay to fix randomness. This thread must be the second.
            Thread.sleep(1);
            throw exception2;
        }).when(command).execute(deviceWrapper2, context);

        try {
            runner.runCommands(devices, context);
        } catch (Throwable e) {
            validateRightExceptionRaisedAndLogged(e, CommandExecutionException.class,
                    NullPointerException.class, exception1);
            verify(logger2).e("DCR", "Execute command exception:", exception2);
            return;
        }
        Assert.fail("Exception must be thrown in this test");
    }
}

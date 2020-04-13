package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Executes commands for online devices.
 */
class DeviceCommandsRunner {
    private static final String TAG = "DCR";
    private final InstrumentalTestPlanProvider testPlanProvider;
    private final DeviceRunnerCommandProvider commandProvider;
    private boolean hasFailedTests;
    private Throwable commandException;

    DeviceCommandsRunner(InstrumentalTestPlanProvider testPlanProvider,
                         DeviceRunnerCommandProvider commandProvider) {
        this.testPlanProvider = testPlanProvider;
        this.commandProvider = commandProvider;
    }

    boolean runCommands(List<ConnectedDeviceWrapper> devices, final TestRunnerContext context) throws InterruptedException,
            CommandExecutionException {
        final CountDownLatch deviceCounter = new CountDownLatch(devices.size());
        final Environment environment = context.getEnvironment();
        for (ConnectedDeviceWrapper device : devices) {
            new Thread(() -> {
                final RunnerLogger logger = device.getLogger();
                logger.i(TAG, "New command execution task started to run commands");
                try {
                    List<DeviceRunnerCommand> commands = commandProvider.provideCommandsForDevice(device,
                            testPlanProvider, environment);
                    for (DeviceRunnerCommand command : commands) {
                        logger.i(TAG, "Before executing command = {}", command.toString());
                        DeviceCommandResult result = command.execute(device, context);
                        logger.i(TAG, "After executing command = {}", command.toString());
                        if (result.isFailed()) {
                            hasFailedTests = true;
                        }
                    }
                } catch (Throwable e) {
                    logger.e(TAG, "Execute command exception:", e);
                    commandException = e;
                } finally {
                    deviceCounter.countDown();
                }
                logger.i(TAG, "Command execution task is finished");
            }).start();
        }
        deviceCounter.await();
        throwExceptionIfNeeded();
        return !hasFailedTests;
    }

    private void throwExceptionIfNeeded() throws CommandExecutionException {
        if (commandException != null) {
            if (commandException instanceof CommandExecutionException) {
                throw (CommandExecutionException) commandException;
            }
            if (commandException instanceof ProcessCrashedException) {
                throw (ProcessCrashedException) commandException;
            }
            throw new CommandExecutionException(commandException);
        }
    }
}

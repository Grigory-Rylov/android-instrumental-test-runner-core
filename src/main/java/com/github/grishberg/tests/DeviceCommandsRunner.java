package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.ExecuteCommandException;
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

    boolean runCommands(ConnectedDeviceWrapper[] devices, final TestRunnerContext context) throws InterruptedException,
            ExecuteCommandException {
        final CountDownLatch deviceCounter = new CountDownLatch(devices.length);
        final Environment environment = context.getEnvironment();
        final RunnerLogger logger = context.getLogger();
        for (ConnectedDeviceWrapper device : devices) {
            new Thread(() -> {
                try {
                    List<DeviceRunnerCommand> commands = commandProvider.provideCommandsForDevice(device,
                            testPlanProvider, environment);
                    for (DeviceRunnerCommand command : commands) {
                        logger.d(TAG, "Before executing device = {} command = {}",
                                device, command.toString());
                        DeviceCommandResult result = command.execute(device, context);
                        logger.d(TAG, "After executing device = {} command = {}",
                                device, command.toString());
                        if (result.isFailed()) {
                            hasFailedTests = true;
                        }
                    }
                } catch (Exception e) {
                    logger.e(TAG, "Execute command exception:", e);
                    commandException = e;
                } finally {
                    deviceCounter.countDown();
                }
            }).start();
        }
        deviceCounter.await();
        throwExceptionIfNeeded();
        return !hasFailedTests;
    }

    private void throwExceptionIfNeeded() throws ExecuteCommandException {
        if (commandException != null) {
            if (commandException instanceof ExecuteCommandException) {
                throw (ExecuteCommandException) commandException;
            }
            if (commandException instanceof ProcessCrashedException) {
                throw (ProcessCrashedException) commandException;
            }
            throw new ExecuteCommandException(commandException);
        }
    }
}

package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.TestListProvider;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Executes commands for online devices.
 */
class SimpleCommandsRunner implements DeviceCommandsRunner {
    private static final String TAG = "DCR";
    private final TestListProvider testPlanProvider;
    private final DeviceRunnerCommandProvider commandProvider;
    private boolean hasFailedTests;
    // Exception from failed child thread (should be rethrown to parent)
    private volatile Throwable commandException;
    private volatile String failedDeviceName;

    SimpleCommandsRunner(TestListProvider testPlanProvider,
                         DeviceRunnerCommandProvider commandProvider) {
        this.testPlanProvider = testPlanProvider;
        this.commandProvider = commandProvider;
    }

    @Override
    public boolean runCommands(
            @NotNull List<? extends ConnectedDeviceWrapper> devices,
            @NotNull TestRunnerContext context) throws InterruptedException, CommandExecutionException {

        final List<TestPlanElement> testsList = testPlanProvider.provideTestList();

        final CountDownLatch deviceCounter = new CountDownLatch(devices.size());
        final Environment environment = context.getEnvironment();
        for (ConnectedDeviceWrapper device : devices) {
            new Thread(() -> {
                final RunnerLogger logger = device.getLogger();
                try {
                    logger.i(TAG, "New command execution task started to run commands");
                    List<DeviceRunnerCommand> commands = commandProvider.provideCommandsForDevice(
                            device,
                            testsList,
                            environment);
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
                    synchronized (SimpleCommandsRunner.this) {
                        // Save exception from the first failed child thread only
                        if (commandException == null) {
                            commandException = e;
                            failedDeviceName = device.getName();
                        }
                    }
                } finally {
                    deviceCounter.countDown();
                }
                logger.i(TAG, "Command execution task is finished");
            }).start();
        }
        // TODO (kindrik): Use await(long timeout, TimeUnit unit) instead
        deviceCounter.await();
        throwExceptionIfNeeded();
        return !hasFailedTests;
    }

    private void throwExceptionIfNeeded() throws CommandExecutionException {
        if (commandException != null) {
            throw new CommandExecutionException(
                    String.format("Exception in child thread (%s)", failedDeviceName),
                    commandException);
        }
    }
}

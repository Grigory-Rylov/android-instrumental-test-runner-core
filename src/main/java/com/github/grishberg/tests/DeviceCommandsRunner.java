package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.DeviceCommandResult;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.logging.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * Executes commands for online devices.
 */
class DeviceCommandsRunner {
    private final InstrumentalTestPlanProvider testPlanProvider;
    private final DeviceRunnerCommandProvider commandProvider;
    private Environment environment;
    private final Logger logger;
    private boolean hasFailedTests;

    DeviceCommandsRunner(InstrumentalTestPlanProvider testPlanProvider,
                         DeviceRunnerCommandProvider commandProvider,
                         Environment directoriesProvider,
                         Logger logger) {
        this.testPlanProvider = testPlanProvider;
        this.commandProvider = commandProvider;
        this.environment = directoriesProvider;
        this.logger = logger;
    }

    boolean runCommands(ConnectedDeviceWrapper[] devices) throws InterruptedException {
        final CountDownLatch deviceCounter = new CountDownLatch(devices.length);

        for (ConnectedDeviceWrapper device : devices) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DeviceRunnerCommand[] commands = commandProvider.provideCommandsForDevice(device,
                                testPlanProvider, environment);
                        for (DeviceRunnerCommand command : commands) {
                            logger.info("[DCR] Before executing device = {} command = {}",
                                    device, command.toString());
                            DeviceCommandResult result = command.execute(device);
                            logger.info("[DCR] After executing device = {} command = {}",
                                    device, command.toString());
                            if (result.isFailed()) {
                                hasFailedTests = true;
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Some Exception", e);
                    } finally {
                        deviceCounter.countDown();
                    }
                }
            }).start();
        }
        deviceCounter.await();
        return !hasFailedTests;
    }
}

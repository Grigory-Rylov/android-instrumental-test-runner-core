package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommand;
import com.github.grishberg.tests.commands.DeviceCommandProvider;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.logging.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * Created by grishberg on 26.10.17.
 */
public class DeviceCommandsRunner {
    private final InstrumentalTestPlanProvider testPlanProvider;
    private final DeviceCommandProvider commandProvider;
    private final Logger logger;

    public DeviceCommandsRunner(InstrumentalTestPlanProvider testPlanProvider,
                                DeviceCommandProvider commandProvider, Logger logger) {
        this.testPlanProvider = testPlanProvider;
        this.commandProvider = commandProvider;
        this.logger = logger;
    }

    public boolean runCommands(DeviceWrapper[] devices) throws InterruptedException {
        final CountDownLatch deviceCounter = new CountDownLatch(devices.length);

        for (DeviceWrapper device : devices) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DeviceCommand[] commands = commandProvider.provideDeviceCommands(device,
                                testPlanProvider);
                        for (DeviceCommand command : commands) {
                            logger.debug("[AITR] Before executing device = {} command ={}",
                                    device.toString(), command.toString());
                            command.execute(device);
                            logger.debug("[AITR] After executing device = {} command ={}",
                                    device.toString(), command.toString());
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
        return true;
    }
}

package com.grishberg.tests;

import com.grishberg.tests.commands.DeviceCommand;
import com.grishberg.tests.commands.DeviceCommandProvider;
import org.gradle.api.logging.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * Created by grishberg on 26.10.17.
 */
public class DeviceCommandsRunner {
    private final DeviceCommandProvider commandProvider;
    private final Logger logger;

    public DeviceCommandsRunner(DeviceCommandProvider commandProvider, Logger logger) {
        this.commandProvider = commandProvider;
        this.logger = logger;
    }

    public boolean runCommands(DeviceWrapper[] devices) throws InterruptedException {
        CountDownLatch deviceCounter = new CountDownLatch(devices.length);
        for (DeviceWrapper device : devices) {
            try {
                for (DeviceCommand command : commandProvider.provideDeviceCommands(device)) {
                    command.execute(device);
                }
            } catch (Exception e) {
                logger.error("Some Exception", e);
            } finally {
                deviceCounter.countDown();
            }
        }
        deviceCounter.await();
        return true;
    }
}

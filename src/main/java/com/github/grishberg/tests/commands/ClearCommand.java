package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import org.gradle.api.logging.Logger;

/**
 * Cleans app data.
 */
public class ClearCommand implements DeviceRunnerCommand {
    private final Logger logger;
    private final InstrumentalPluginExtension instrumentationInfo;

    public ClearCommand(Logger logger, InstrumentalPluginExtension instrumentalInfo) {
        this.logger = logger;
        this.instrumentationInfo = instrumentalInfo;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device) throws ExecuteCommandException {
        logger.info("ClearCommand for package {}", instrumentationInfo.getApplicationId());
        StringBuilder command = new StringBuilder("pm clear ");
        command.append(instrumentationInfo.getApplicationId());

        device.executeShellCommand(command.toString());
        return new DeviceCommandResult();
    }
}

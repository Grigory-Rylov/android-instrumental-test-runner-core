package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.common.RunnerLogger;

/**
 * Cleans app data.
 */
public class ClearCommand implements DeviceRunnerCommand {
    private static final String TAG = ClearCommand.class.getSimpleName();
    private final RunnerLogger logger;
    private final InstrumentalPluginExtension instrumentationInfo;

    public ClearCommand(RunnerLogger logger, InstrumentalPluginExtension instrumentalInfo) {
        this.logger = logger;
        this.instrumentationInfo = instrumentalInfo;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device) throws ExecuteCommandException {
        logger.i(TAG, "ClearCommand for package {}", instrumentationInfo.getApplicationId());
        StringBuilder command = new StringBuilder("pm clear ");
        command.append(instrumentationInfo.getApplicationId());

        device.executeShellCommand(command.toString());
        return new DeviceCommandResult();
    }
}

package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;

/**
 * Cleans app data.
 */
public class ClearCommand implements DeviceRunnerCommand {
    private static final String TAG = ClearCommand.class.getSimpleName();

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device, TestRunnerContext context)
            throws CommandExecutionException {
        InstrumentalExtension instrumentalInfo = context.getInstrumentalInfo();
        device.getLogger().i(TAG, "ClearCommand for package {}",
                instrumentalInfo.getApplicationId());
        StringBuilder command = new StringBuilder("pm clear ");
        command.append(instrumentalInfo.getApplicationId());

        device.executeShellCommand(command.toString());
        return new DeviceCommandResult();
    }
}

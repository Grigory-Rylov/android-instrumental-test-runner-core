package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;

import javax.annotation.Nullable;

/**
 * Cleans app data.
 */
public class ClearCommand implements DeviceRunnerCommand {
    private static final String TAG = ClearCommand.class.getSimpleName();

    @Nullable
    private final String appId;

    @Deprecated
    public ClearCommand() {
        appId = null;
    }

    public ClearCommand(String appId) {
        this.appId = appId;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device, TestRunnerContext context)
            throws CommandExecutionException {
        String appIdToClear = (appId != null) ?
                appId : context.getInstrumentalInfo().getApplicationId();
        device.getLogger().d(TAG, "ClearCommand for package {}", appIdToClear);
        StringBuilder command = new StringBuilder("pm clear ");
        command.append(appIdToClear);

        device.executeShellCommand(command.toString());
        return new DeviceCommandResult();
    }

    @Override
    public String toString() {
        String appIdToClear = (appId != null) ? appId : "<UNSPECIFIED>";
        return this.getClass().getSimpleName() + "{Remove package from device: " +
                appIdToClear +
                "}";
    }

}

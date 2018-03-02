package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;

/**
 * Executes some task on device.
 */
public interface DeviceRunnerCommand {
    DeviceCommandResult execute(ConnectedDeviceWrapper device) throws ExecuteCommandException;
}

package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.TestRunnerContext;

/**
 * Executes some task on device.
 */
public interface DeviceRunnerCommand {
    DeviceCommandResult execute(ConnectedDeviceWrapper device, TestRunnerContext context)
            throws CommandExecutionException;
}

package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.DeviceWrapper;

/**
 * Executes some task on device.
 */
public interface DeviceCommand {
    DeviceCommandResult execute(DeviceWrapper device);
}

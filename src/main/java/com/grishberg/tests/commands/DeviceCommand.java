package com.grishberg.tests.commands;

import com.grishberg.tests.DeviceWrapper;

/**
 * Executes some task on device.
 */
public interface DeviceCommand {
    DeviceCommandResult execute(DeviceWrapper device);
}

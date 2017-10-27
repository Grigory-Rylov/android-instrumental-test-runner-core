package com.grishberg.tests.commands;

import com.grishberg.tests.DeviceWrapper;

/**
 * Provides commands list for current device.
 */
public interface DeviceCommandProvider {
    DeviceCommand[] provideDeviceCommands(DeviceWrapper device);
}

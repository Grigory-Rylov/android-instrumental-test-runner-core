package com.grishberg.tests.commands;

import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.planner.InstrumentalTestPlanProvider;

/**
 * Provides commands list for current device.
 */
public interface DeviceCommandProvider {
    DeviceCommand[] provideDeviceCommands(DeviceWrapper device,
                                          InstrumentalTestPlanProvider testPlanProvider);
}

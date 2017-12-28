package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.DeviceWrapper;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

import java.io.File;

/**
 * Provides commands list for current device.
 */
public interface DeviceCommandProvider {
    DeviceCommand[] provideDeviceCommands(DeviceWrapper device,
                                          InstrumentalTestPlanProvider testPlanProvider,
                                          File coverageFilesDir, File reportsDir);
}

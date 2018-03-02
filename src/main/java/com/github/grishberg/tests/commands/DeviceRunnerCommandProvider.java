package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

/**
 * Provides commands list for current device.
 */
public interface DeviceRunnerCommandProvider {
    DeviceRunnerCommand[] provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                   InstrumentalTestPlanProvider testPlanProvider,
                                                   Environment environment);
}

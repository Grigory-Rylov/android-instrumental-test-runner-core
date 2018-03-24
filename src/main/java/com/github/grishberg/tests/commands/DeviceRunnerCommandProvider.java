package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

import java.util.List;

/**
 * Provides commands list for current device.
 */
public interface DeviceRunnerCommandProvider {
    List<DeviceRunnerCommand> provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                       InstrumentalTestPlanProvider testPlanProvider,
                                                       Environment environment);
}

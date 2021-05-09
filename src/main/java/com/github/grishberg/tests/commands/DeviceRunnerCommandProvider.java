package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.planner.TestPlanElement;

import java.util.List;

/**
 * Provides commands list for current device.
 */
public interface DeviceRunnerCommandProvider {
    /**
     * @param device      target device.
     * @param tests       list of tests.
     * @param environment environments folders.
     * @return List of commands to be executed on device.
     * @throws CommandExecutionException
     */
    List<DeviceRunnerCommand> provideCommandsForDevice(
            ConnectedDeviceWrapper device,
            List<TestPlanElement> tests,
            Environment environment) throws CommandExecutionException;
}

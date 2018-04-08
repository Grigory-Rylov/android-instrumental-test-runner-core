package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

/**
 * Created by grishberg on 30.03.18.
 */
public class DeviceCommandsRunnerFabric {
    private final InstrumentalTestPlanProvider testPlanProvider;

    public DeviceCommandsRunnerFabric(InstrumentalTestPlanProvider testPlanProvider) {
        this.testPlanProvider = testPlanProvider;
    }

    DeviceCommandsRunner provideDeviceCommandRunner(DeviceRunnerCommandProvider commandProvider) {
        return new DeviceCommandsRunner(testPlanProvider, commandProvider);
    }
}

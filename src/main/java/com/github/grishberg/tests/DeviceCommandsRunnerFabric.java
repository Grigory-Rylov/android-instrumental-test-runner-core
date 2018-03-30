package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

/**
 * Created by grishberg on 30.03.18.
 */
public class DeviceCommandsRunnerFabric {
    private final RunnerLogger logger;
    private final InstrumentalTestPlanProvider testPlanProvider;

    public DeviceCommandsRunnerFabric(RunnerLogger logger,
                                      InstrumentalTestPlanProvider testPlanProvider) {
        this.logger = logger;
        this.testPlanProvider = testPlanProvider;
    }

    DeviceCommandsRunner provideDeviceCommandRunner(DeviceRunnerCommandProvider commandProvider,
                                                    Environment environment) {
        return new DeviceCommandsRunner(testPlanProvider, commandProvider,
                environment, logger);
    }
}

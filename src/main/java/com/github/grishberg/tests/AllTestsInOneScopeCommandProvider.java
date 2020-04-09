package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.InstrumentalTestCommand;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides one command for all instrumental tests for device.
 */
public class AllTestsInOneScopeCommandProvider implements DeviceRunnerCommandProvider {
    private static final String TAG = AllTestsInOneScopeCommandProvider.class.getSimpleName();
    private final String projectName;
    private final InstrumentationArgsProvider argsProvider;

    public AllTestsInOneScopeCommandProvider(String projectName,
                                             InstrumentationArgsProvider argsProvider) {
        this.projectName = projectName;
        this.argsProvider = argsProvider;
    }

    @Override
    public List<DeviceRunnerCommand> provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                              InstrumentalTestPlanProvider testPlanProvider,
                                                              TestRunnerContext context) {
        RunnerLogger logger = context.getLogger();
        List<DeviceRunnerCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);
        logger.i(TAG, "device = {}, args = {}",
                device, instrumentalArgs);

        commands.add(new InstrumentalTestCommand(projectName, instrumentalArgs));
        return commands;
    }
}

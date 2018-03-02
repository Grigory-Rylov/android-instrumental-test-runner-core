package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.InstrumentalTestCommand;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides one command for all instrumental tests for device.
 */
public class AllTestsInOneScopeCommandProvider implements DeviceRunnerCommandProvider {
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;

    public AllTestsInOneScopeCommandProvider(Project project,
                                             InstrumentalPluginExtension instrumentalInfo,
                                             InstrumentationArgsProvider argsProvider) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.argsProvider = argsProvider;
    }

    @Override
    public DeviceRunnerCommand[] provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                          InstrumentalTestPlanProvider testPlanProvider,
                                                          Environment environment) {
        List<DeviceRunnerCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);
        project.getLogger().info("[AllTestsInOneScopeCommandProvider] device={}, args={}",
                device, instrumentalArgs);

        commands.add(new InstrumentalTestCommand(project,
                instrumentationInfo,
                instrumentalArgs,
                environment.getCoverageDir(),
                environment.getReportsDir()));
        return commands.toArray(new DeviceRunnerCommand[commands.size()]);
    }
}

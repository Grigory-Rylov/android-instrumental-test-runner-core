package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.parser.TestPlan;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides commands for device.
 */
public class DefaultCommandProvider implements DeviceRunnerCommandProvider {
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;
    private final CommandsForAnnotationProvider commandsForAnnotationProvider;

    public DefaultCommandProvider(Project project,
                                  InstrumentalPluginExtension instrumentalInfo,
                                  InstrumentationArgsProvider argsProvider,
                                  CommandsForAnnotationProvider commandsForAnnotationProvider) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.argsProvider = argsProvider;
        this.commandsForAnnotationProvider = commandsForAnnotationProvider;
    }

    @Override
    public DeviceRunnerCommand[] provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                          InstrumentalTestPlanProvider testPlanProvider,
                                                          Environment environment) {
        List<DeviceRunnerCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);
        project.getLogger().info("[DefaultCommandProvider] device={}, args={}",
                device, instrumentalArgs);
        Set<TestPlan> planSet = testPlanProvider.provideTestPlan(device, instrumentalArgs);

        for (TestPlan currentPlan : planSet) {

            List<DeviceRunnerCommand> commandsForAnnotations = commandsForAnnotationProvider
                    .provideCommand(currentPlan.getAnnotations());
            commands.addAll(commandsForAnnotations);
            commands.add(new SingleInstrumentalTestCommand(project,
                    instrumentationInfo,
                    instrumentalArgs,
                    currentPlan,
                    environment.getCoverageDir(),
                    environment.getResultsDir()));
        }

        return commands.toArray(new DeviceRunnerCommand[commands.size()]);
    }
}

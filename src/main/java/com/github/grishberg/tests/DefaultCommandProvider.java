package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommand;
import com.github.grishberg.tests.commands.DeviceCommandProvider;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.parser.TestPlan;
import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides commands for device.
 */
public class DefaultCommandProvider implements DeviceCommandProvider {
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
    public DeviceCommand[] provideDeviceCommands(DeviceWrapper device,
                                                 InstrumentalTestPlanProvider testPlanProvider,
                                                 File coverageFilesDir, File reportsDir) {
        List<DeviceCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);
        project.getLogger().debug("[AITR] device={}, args={}",
                device.toString(), instrumentalArgs);
        Set<TestPlan> planSet = testPlanProvider.provideTestPlan(device, instrumentalArgs);

        for (TestPlan currentPlan : planSet) {

            List<DeviceCommand> commandsForAnnotations = commandsForAnnotationProvider
                    .provideCommand(currentPlan.getAnnotations());
            commands.addAll(commandsForAnnotations);
            commands.add(new SingleInstrumentalTestCommand(project,
                    instrumentationInfo,
                    instrumentalArgs,
                    currentPlan));
        }

        return commands.toArray(new DeviceCommand[commands.size()]);
    }
}

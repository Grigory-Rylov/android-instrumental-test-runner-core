package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommand;
import com.github.grishberg.tests.commands.DeviceCommandProvider;
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
public class DefaultCommandProvider implements DeviceCommandProvider {
    private final Project project;
    private final InstrumentationInfo instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;
    private final CommandsForAnnotationProvider commandsForAnnotationProvider;

    public DefaultCommandProvider(Project project,
                                  InstrumentationInfo instrumentalInfo,
                                  InstrumentationArgsProvider argsProvider,
                                  CommandsForAnnotationProvider commandsForAnnotationProvider) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.argsProvider = argsProvider;
        this.commandsForAnnotationProvider = commandsForAnnotationProvider;
    }

    @Override
    public DeviceCommand[] provideDeviceCommands(DeviceWrapper device,
                                                 InstrumentalTestPlanProvider testPlanProvider) {
        List<DeviceCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);

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

        //TODO: group tests by classes without annotations

        return commands.toArray(new DeviceCommand[commands.size()]);
    }
}

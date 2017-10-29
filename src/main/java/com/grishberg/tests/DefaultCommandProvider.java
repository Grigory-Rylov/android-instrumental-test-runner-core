package com.grishberg.tests;

import com.grishberg.tests.commands.*;
import com.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.grishberg.tests.planner.parser.TestPlan;
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

    public DefaultCommandProvider(Project project,
                                  InstrumentationInfo instrumentalInfo,
                                  InstrumentationArgsProvider argsProvider) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.argsProvider = argsProvider;
    }

    @Override
    public DeviceCommand[] provideDeviceCommands(DeviceWrapper device,
                                                 InstrumentalTestPlanProvider testPlanProvider) {
        List<DeviceCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);

        Set<TestPlan> planSet = testPlanProvider.provideTestPlan(device, instrumentalArgs);
        for (TestPlan currentPlan : planSet) {

            for (DeviceCommand additionalCommand : processAnnotations(currentPlan.getAnnotations())) {
                commands.add(additionalCommand);
            }
            commands.add(new SingleInstrumentalTestCommand(project,
                    instrumentationInfo,
                    instrumentalArgs,
                    currentPlan));
        }

        commands.add(new InstrumentalTestCommand(project, instrumentationInfo, instrumentalArgs));

        return commands.toArray(new DeviceCommand[commands.size()]);
    }

    private List<DeviceCommand> processAnnotations(String[] annotations) {
        ArrayList<DeviceCommand> commands = new ArrayList<>();
        for (String annotation : annotations) {
            if ("ClearData".equals(annotation)) {
                commands.add(new ClearCommand(project, instrumentationInfo));
            }
        }
        return commands;
    }
}

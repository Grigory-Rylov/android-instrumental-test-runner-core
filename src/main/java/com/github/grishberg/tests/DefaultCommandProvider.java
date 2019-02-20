package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.*;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.TestPlanElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides commands for device.
 */
public class DefaultCommandProvider implements DeviceRunnerCommandProvider {
    private static final String TAG = DefaultCommandProvider.class.getSimpleName();
    private final String projectName;
    private final InstrumentationArgsProvider argsProvider;
    private final CommandsForAnnotationProvider commandsForAnnotationProvider;
    private final RunnerLogger logger;

    DefaultCommandProvider(String projectName,
                           InstrumentationArgsProvider argsProvider,
                           CommandsForAnnotationProvider commandsForAnnotationProvider,
                           RunnerLogger logger) {
        this.projectName = projectName;
        this.argsProvider = argsProvider;
        this.commandsForAnnotationProvider = commandsForAnnotationProvider;
        this.logger = logger;
    }

    @Override
    public List<DeviceRunnerCommand> provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                              InstrumentalTestPlanProvider testPlanProvider,
                                                              Environment environment) throws CommandExecutionException {
        List<DeviceRunnerCommand> commands = new ArrayList<>();
        commands.add(new SetAnimationSpeedCommand(0, 0, 0));
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);
        logger.i(TAG, "provideCommandsForDevice: device = {}, args = {}",
                device, instrumentalArgs);
        List<TestPlanElement> planSet = testPlanProvider.provideTestPlan(device, instrumentalArgs);

        List<TestPlanElement> planList = new ArrayList<>();
        int testIndex = 0;
        for (TestPlanElement currentPlan : planSet) {
            List<DeviceRunnerCommand> commandsForAnnotations = commandsForAnnotationProvider
                    .provideCommand(currentPlan.getAnnotations());
            if (!commandsForAnnotations.isEmpty()) {
                if (!planList.isEmpty()) {
                    commands.add(new SingleInstrumentalTestCommand(projectName,
                            String.format("test_%d", testIndex++),
                            instrumentalArgs,
                            planList));
                    planList.clear();
                }

                commands.addAll(commandsForAnnotations);
            }
            planList.add(currentPlan);
        }

        if (!planList.isEmpty()) {
            commands.add(new SingleInstrumentalTestCommand(projectName,
                    String.format("test_%d", testIndex),
                    instrumentalArgs,
                    planList));
        }
        commands.add(new SetAnimationSpeedCommand(1, 1, 1));
        return commands;
    }
}

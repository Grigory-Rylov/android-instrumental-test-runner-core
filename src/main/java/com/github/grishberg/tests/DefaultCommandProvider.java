package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.parser.TestPlanElement;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides commands for device.
 */
public class DefaultCommandProvider implements DeviceRunnerCommandProvider {
    private static final String TAG = DefaultCommandProvider.class.getSimpleName();
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final InstrumentationArgsProvider argsProvider;
    private final CommandsForAnnotationProvider commandsForAnnotationProvider;
    private final RunnerLogger logger;

    public DefaultCommandProvider(Project project,
                                  InstrumentalPluginExtension instrumentalInfo,
                                  InstrumentationArgsProvider argsProvider,
                                  CommandsForAnnotationProvider commandsForAnnotationProvider, RunnerLogger logger) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.argsProvider = argsProvider;
        this.commandsForAnnotationProvider = commandsForAnnotationProvider;
        this.logger = logger;
    }

    @Override
    public DeviceRunnerCommand[] provideCommandsForDevice(ConnectedDeviceWrapper device,
                                                          InstrumentalTestPlanProvider testPlanProvider,
                                                          Environment environment) {
        List<DeviceRunnerCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);
        logger.i(TAG, "device = %s, args = %s",
                device, instrumentalArgs);
        List<TestPlanElement> planSet = testPlanProvider.provideTestPlan(device, instrumentalArgs);

        List<TestPlanElement> planList = new ArrayList<>();
        int testIndex = 0;
        for (TestPlanElement currentPlan : planSet) {
            List<DeviceRunnerCommand> commandsForAnnotations = commandsForAnnotationProvider
                    .provideCommand(currentPlan.getAnnotations());
            if (!commandsForAnnotations.isEmpty() && !planList.isEmpty()) {
                commands.addAll(commandsForAnnotations);
                commands.add(new SingleInstrumentalTestCommand(project,
                        String.format("test_%d", testIndex++),
                        instrumentationInfo,
                        instrumentalArgs,
                        planList,
                        environment.getCoverageDir(),
                        environment.getResultsDir(),
                        logger));
                planList.clear();
                continue;
            }
            planList.add(currentPlan);
        }

        return commands.toArray(new DeviceRunnerCommand[commands.size()]);
    }
}

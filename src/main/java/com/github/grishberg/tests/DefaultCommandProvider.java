package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.TestNodeElement;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        TestNodeElement testRoot = testPlanProvider.provideTestRootNode(device, instrumentalArgs);

        List<TestNodeElement> testMethods = testRoot.getAllTestMethods();

        for (TestNodeElement currentTestMethod : testMethods) {

            List<DeviceRunnerCommand> commandsForAnnotations = commandsForAnnotationProvider
                    .provideCommand(currentTestMethod.getAnnotations());
            if (commandsForAnnotations.size() > 0) {
                commands.addAll(commandsForAnnotations);
                commands.add(new SingleInstrumentalTestCommand(project,
                        instrumentationInfo,
                        instrumentalArgs,
                        currentTestMethod.getAmInstrumentPath(),
                        environment.getCoverageDir(),
                        environment.getResultsDir()));
                // call currentTestMethod.excluded() if you want to exclude current test from package tree
                // for example, you need to change order for current test, or you dont want execute current
                // test on current device.
                currentTestMethod.excluded();
            }
        }

        List<TestNodeElement> compoundTestNodes = testRoot.getCommandsForAmInstrument();
        for (TestNodeElement currentTestPackage : compoundTestNodes) {
            commands.add(new SingleInstrumentalTestCommand(project,
                    instrumentationInfo,
                    instrumentalArgs,
                    currentTestPackage.getAmInstrumentPath(),
                    environment.getCoverageDir(),
                    environment.getResultsDir()));
        }
        return commands.toArray(new DeviceRunnerCommand[commands.size()]);
    }
}

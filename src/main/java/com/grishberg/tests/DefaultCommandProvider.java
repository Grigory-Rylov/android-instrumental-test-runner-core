package com.grishberg.tests;

import com.grishberg.tests.commands.DeviceCommand;
import com.grishberg.tests.commands.DeviceCommandProvider;
import com.grishberg.tests.commands.InstrumentalTestCommand;
import com.grishberg.tests.commands.InstrumentationLogCommand;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public DeviceCommand[] provideDeviceCommands(DeviceWrapper device) {
        List<DeviceCommand> commands = new ArrayList<>();
        Map<String, String> instrumentalArgs = argsProvider.provideInstrumentationArgs(device);

        commands.add(new InstrumentationLogCommand(project, instrumentationInfo, instrumentalArgs));

        commands.add(new InstrumentalTestCommand(project, instrumentationInfo, instrumentalArgs));

        return commands.toArray(new DeviceCommand[commands.size()]);
    }
}

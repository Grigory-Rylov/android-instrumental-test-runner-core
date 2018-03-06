package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.ClearCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import org.gradle.api.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides ClearCommand for CleanData annotation.
 */
public class DefaultCommandsForAnnotationProvider implements CommandsForAnnotationProvider {
    private final Logger logger;
    private final InstrumentalPluginExtension instrumentationInfo;

    public DefaultCommandsForAnnotationProvider(Logger logger,
                                                InstrumentalPluginExtension instrumentationInfo) {
        this.logger = logger;
        this.instrumentationInfo = instrumentationInfo;
    }

    @Override
    public List<DeviceRunnerCommand> provideCommand(List<String> annotations) {
        ArrayList<DeviceRunnerCommand> commands = new ArrayList<>();
        for (String annotation : annotations) {
            if ("ClearData".equals(annotation)) {
                commands.add(new ClearCommand(logger, instrumentationInfo));
            }
        }
        return commands;
    }
}

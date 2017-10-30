package com.grishberg.tests;

import com.grishberg.tests.commands.ClearCommand;
import com.grishberg.tests.commands.DeviceCommand;
import org.gradle.api.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides ClearCommand for CleanData annotation.
 */
public class DefaultCommandsForAnnotationProvider implements CommandsForAnnotationProvider {
    private final Logger logger;
    private final InstrumentationInfo instrumentationInfo;

    public DefaultCommandsForAnnotationProvider(Logger logger,
                                                InstrumentationInfo instrumentationInfo) {
        this.logger = logger;
        this.instrumentationInfo = instrumentationInfo;
    }

    @Override
    public List<DeviceCommand> provideCommand(String[] annotations) {
        ArrayList<DeviceCommand> commands = new ArrayList<>();
        for (String annotation : annotations) {
            if ("ClearData".equals(annotation)) {
                commands.add(new ClearCommand(logger, instrumentationInfo));
            }
        }
        return commands;
    }
}

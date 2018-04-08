package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.ClearCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides ClearCommand for CleanData annotation.
 */
public class DefaultCommandsForAnnotationProvider implements CommandsForAnnotationProvider {
    @Override
    public List<DeviceRunnerCommand> provideCommand(List<String> annotations) {
        ArrayList<DeviceRunnerCommand> commands = new ArrayList<>();
        if (annotations == null) {
            return commands;
        }

        for (String annotation : annotations) {
            if ("ClearData".equals(annotation)) {
                commands.add(new ClearCommand());
            }
        }
        return commands;
    }
}

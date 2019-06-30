package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.ClearCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.planner.AnnotationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides ClearCommand for CleanData annotation.
 */
public class DefaultCommandsForAnnotationProvider implements CommandsForAnnotationProvider {
    @Override
    public List<DeviceRunnerCommand> provideCommand(List<AnnotationInfo> annotations) {
        ArrayList<DeviceRunnerCommand> commands = new ArrayList<>();
        if (annotations == null) {
            return commands;
        }

        for (AnnotationInfo annotation : annotations) {
            if ("com.github.grishberg.tests.annotations.ClearData".equals(annotation.getName())) {
                commands.add(new ClearCommand());
            }
        }
        return commands;
    }
}

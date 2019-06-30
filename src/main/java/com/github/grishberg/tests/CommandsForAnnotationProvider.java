package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.planner.AnnotationInfo;

import java.util.List;

/**
 * Provides command for annotation.
 */
public interface CommandsForAnnotationProvider {
    List<DeviceRunnerCommand> provideCommand(List<AnnotationInfo> annotation);
}

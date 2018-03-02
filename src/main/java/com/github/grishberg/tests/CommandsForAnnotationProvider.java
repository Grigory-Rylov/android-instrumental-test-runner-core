package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;

import java.util.List;

/**
 * Provides command for annotation.
 */
public interface CommandsForAnnotationProvider {
    List<DeviceRunnerCommand> provideCommand(String[] annotation);
}

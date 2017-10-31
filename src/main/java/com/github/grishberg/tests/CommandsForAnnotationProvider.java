package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceCommand;

import java.util.List;

/**
 * Provides command for annotation.
 */
public interface CommandsForAnnotationProvider {
    List<DeviceCommand> provideCommand(String[] annotation);
}

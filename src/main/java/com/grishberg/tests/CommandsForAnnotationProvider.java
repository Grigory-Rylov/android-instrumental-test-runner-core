package com.grishberg.tests;

import com.grishberg.tests.commands.DeviceCommand;

import java.util.List;

/**
 * Provides command for annotation.
 */
public interface CommandsForAnnotationProvider {
    List<DeviceCommand> provideCommand(String[] annotation);
}

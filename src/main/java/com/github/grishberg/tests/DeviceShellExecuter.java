package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.CommandExecutionException;

/**
 * Created by grishberg on 05.04.18.
 */
public interface DeviceShellExecuter {
    void executeShellCommand(String command) throws CommandExecutionException;

    String executeShellCommandAndReturnOutput(String command) throws CommandExecutionException;

    void pullFile(String temporaryCoverageCopy, String path) throws CommandExecutionException;

    void pushFile(String localPath, String remotePath) throws CommandExecutionException;

    String getName();
}

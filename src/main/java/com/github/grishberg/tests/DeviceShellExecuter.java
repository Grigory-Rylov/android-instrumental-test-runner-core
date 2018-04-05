package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.ExecuteCommandException;

/**
 * Created by grishberg on 05.04.18.
 */
public interface DeviceShellExecuter {
    void executeShellCommand(String command) throws ExecuteCommandException;

    void pullFile(String temporaryCoverageCopy, String path) throws ExecuteCommandException;

    String getName();
}

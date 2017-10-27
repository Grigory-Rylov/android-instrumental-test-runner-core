package com.grishberg.tests.commands;

/**
 * Result of DeviceCommand execution.
 */
public class DeviceCommandResult {
    private boolean isFailed;

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }
}

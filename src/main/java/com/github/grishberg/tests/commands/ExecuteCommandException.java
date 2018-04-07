package com.github.grishberg.tests.commands;

/**
 * Exception while executing command on device.
 */
public class ExecuteCommandException extends Exception {
    public ExecuteCommandException(String message, Throwable e) {
        super(message, e);
    }

    public ExecuteCommandException(Throwable e) {
        super(e);
    }
}

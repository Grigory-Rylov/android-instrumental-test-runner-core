package com.github.grishberg.tests.commands;

/**
 * Exception while executing command on device.
 */
public class CommandExecutionException extends Exception {
    public CommandExecutionException(String message, Throwable e) {
        super(message, e);
    }

    public CommandExecutionException(Throwable e) {
        super(e);
    }
}

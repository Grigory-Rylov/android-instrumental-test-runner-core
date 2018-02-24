package com.github.grishberg.tests.commands;

/**
 * Created by grishberg on 24.02.18.
 */
public class ExecuteCommandException extends Exception {
    public ExecuteCommandException(String message, Throwable e) {
        super(message, e);
    }
}

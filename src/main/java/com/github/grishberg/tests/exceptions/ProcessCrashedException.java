package com.github.grishberg.tests.exceptions;

/**
 * Created by grishberg on 09.04.18.
 */
public class ProcessCrashedException extends RuntimeException {
    public ProcessCrashedException(String message) {
        super(message);
    }
}

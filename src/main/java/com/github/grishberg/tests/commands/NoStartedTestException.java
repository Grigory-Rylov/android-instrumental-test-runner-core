package com.github.grishberg.tests.commands;

/**
 * This error can be raised when "am instrument" command crashed before any test could be started.
 * That happens when tested application crashes too early.
 * In such conditions our test runner cannot generate reasonable XML report because the failing
 * tests are unknown - they were not run.
 * Underlying test runner must handle this case in a reasonable way.
 */
public class NoStartedTestException extends CommandExecutionException {
    public NoStartedTestException(String message) {
        super(message);
    }
}

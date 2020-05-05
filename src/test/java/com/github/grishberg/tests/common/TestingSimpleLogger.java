package com.github.grishberg.tests.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simplified logger for testing.
 *
 * It is recommended to use this class or spy(TestingSimpleLogger()) in tests for comfortable work
 * instead of default RunnerLogger.Stub or mock(RunnerLogger.class) which log nothing.
 */
public class TestingSimpleLogger implements RunnerLogger {
    @Override
    public void w(String tag, String message) {
        log("E:", tag, message);
    }

    @Override
    public void i(String tag, String message) {
        log("I:", tag, message);
    }

    @Override
    public void i(String tag, String msgFormat, Object... args) {
        log("I:", tag, msgFormat, args);
    }

    @Override
    public void d(String tag, String message) {
        log("D:", tag, message);
    }

    @Override
    public void d(String tag, String msgFormat, Object... args) {
        log("D:", tag, msgFormat, args);
    }

    @Override
    public void e(String tag, String message) {
        log("E:", tag, message);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        log("E:", tag, message, throwable);
    }

    @Override
    public void w(String tag, String msgFormat, Object... args) {
        log("W:", tag, msgFormat, args);
    }

    private void log(String level, String tag, String message) {
        System.out.println(String.format("%s [%s] %s", level, tag, message));
    }

    private void log(String level, String tag, String message, Throwable throwable) {
        StringWriter out = new StringWriter();
        throwable.printStackTrace(new PrintWriter(out));
        System.out.println(String.format("%s [%s] %s %s", level, tag, message, out));
    }

    private void log(String level, String tag, String msgFormat, Object[] args) {
        List<String> stringArgs =
                Arrays.stream(args).map( it -> it == null ? "null" : it.toString() ).collect(
                        Collectors.toList());
        System.out.println(String.format("%s [%s] %s, ARGS = [%s]", level, tag, msgFormat,
                String.join(", ", stringArgs)));
    }

}

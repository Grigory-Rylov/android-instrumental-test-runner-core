package com.github.grishberg.tests.common;

import org.gradle.api.logging.Logger;

/**
 * Default RunnerLogger implementation.
 */
public class DefaultGradleLogger implements RunnerLogger {
    private static final String FORMAT_STR = "{}: {}";
    private final Logger logger;

    public DefaultGradleLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void w(String tag, String message) {
        logger.warn(FORMAT_STR, tag, message);
    }

    @Override
    public void i(String tag, String message) {
        logger.info(FORMAT_STR, tag, message);
    }

    @Override
    public void i(String tag, String msgFormat, Object... args) {
        if (msgFormat == null) {
            return;
        }
        logger.info(FORMAT_STR, tag, String.format(msgFormat, args));
    }

    @Override
    public void d(String tag, String message) {
        logger.debug(FORMAT_STR, tag, message);
    }

    @Override
    public void d(String tag, String msgFormat, Object... args) {
        if (msgFormat == null) {
            return;
        }
        logger.debug(FORMAT_STR, tag, String.format(msgFormat, args));
    }

    @Override
    public void e(String tag, String message) {
        logger.error(FORMAT_STR, tag, message);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        logger.error(FORMAT_STR, tag, message, throwable);
    }
}

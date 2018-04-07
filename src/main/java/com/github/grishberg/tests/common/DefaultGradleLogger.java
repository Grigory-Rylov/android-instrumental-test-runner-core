package com.github.grishberg.tests.common;

import org.gradle.api.logging.Logger;

/**
 * Default RunnerLogger implementation.
 */
public class DefaultGradleLogger implements RunnerLogger {
    private static final String FORMAT_STR = "{}: {}";
    private static final String TAG_DIVIDER = ": ";
    private final Logger logger;

    public DefaultGradleLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void w(String tag, String message) {
        logger.warn(FORMAT_STR, tag, message);
    }

    @Override
    public void w(String tag, String msgFormat, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(tag + TAG_DIVIDER + msgFormat, args);
        }
    }

    @Override
    public void i(String tag, String message) {
        if (logger.isInfoEnabled()) {
            logger.info(FORMAT_STR, tag, message);
        }
    }

    @Override
    public void i(String tag, String msgFormat, Object... args) {
        if (msgFormat == null || !logger.isInfoEnabled()) {
            return;
        }
        logger.info(tag + TAG_DIVIDER + msgFormat, args);
    }

    @Override
    public void d(String tag, String message) {
        logger.debug(FORMAT_STR, tag, message);
    }

    @Override
    public void d(String tag, String msgFormat, Object... args) {
        if (msgFormat == null || !logger.isDebugEnabled()) {
            return;
        }
        logger.debug(tag + TAG_DIVIDER + msgFormat, args);
    }

    @Override
    public void e(String tag, String message) {
        logger.error(FORMAT_STR, tag, message);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        logger.error(String.format("%s: %s", tag, message), throwable);
    }
}

package com.github.grishberg.tests.common;

import org.gradle.api.logging.Logger;

/**
 * Default RunnerLogger implementation.
 */
public class DefaultGradleLogger implements RunnerLogger {
    private final Logger logger;

    public DefaultGradleLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void w(String tag, String message) {
        logger.warn("{}: {}", tag, message);
    }

    @Override
    public void i(String tag, String message) {
        logger.info("{}: {}", tag, message);
    }

    @Override
    public void i(String tag, String msgFormat, Object... args) {
        logger.info("{}: {}", tag, String.format(msgFormat, args));
    }

    @Override
    public void d(String tag, String message) {
        logger.debug("{}: {}", tag, message);
    }

    @Override
    public void d(String tag, String msgFormat, Object... args) {
        logger.info("{}: {}", tag, String.format(msgFormat, args));
    }

    @Override
    public void e(String tag, String message) {
        logger.error("{}: {}", tag, message);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        logger.error("{}: {}", tag, message, throwable);
    }
}

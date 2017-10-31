package com.github.grishberg.tests;

import com.android.utils.ILogger;
import org.gradle.api.logging.Logger;

/**
 * Created by grishberg on 29.10.17.
 */
public class RunTestLogger implements ILogger {
    private final Logger logger;

    public RunTestLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void error(Throwable t, String msgFormat, Object... args) {
        logger.error(String.format(msgFormat, args), t);
    }

    @Override
    public void warning(String msgFormat, Object... args) {
        logger.warn(String.format(msgFormat, args));
    }

    @Override
    public void info(String msgFormat, Object... args) {
        logger.info(String.format(msgFormat, args));
    }

    @Override
    public void verbose(String msgFormat, Object... args) {
        logger.info(String.format(msgFormat, args));
    }
}

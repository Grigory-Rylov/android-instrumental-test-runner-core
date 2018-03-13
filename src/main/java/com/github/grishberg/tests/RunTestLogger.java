package com.github.grishberg.tests;

import com.android.utils.ILogger;
import com.github.grishberg.tests.common.RunnerLogger;

/**
 * Created by grishberg on 29.10.17.
 */
public class RunTestLogger implements ILogger {
    private static final String TAG = RunTestLogger.class.getSimpleName();
    private final RunnerLogger logger;

    public RunTestLogger(RunnerLogger logger) {
        this.logger = logger;
    }

    @Override
    public void error(Throwable t, String msgFormat, Object... args) {
        logger.e(TAG, String.format(msgFormat, args), t);
    }

    @Override
    public void warning(String msgFormat, Object... args) {
        logger.w(TAG, String.format(msgFormat, args));
    }

    @Override
    public void info(String msgFormat, Object... args) {
        logger.i(TAG, String.format(msgFormat, args));
    }

    @Override
    public void verbose(String msgFormat, Object... args) {
        logger.i(TAG, String.format(msgFormat, args));
    }
}

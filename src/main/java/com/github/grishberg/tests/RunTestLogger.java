package com.github.grishberg.tests;

import com.android.utils.ILogger;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;

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
        if (msgFormat == null) {
            return;
        }
        String message = formatMessageIfNeeded(msgFormat, args);
        logger.e(TAG, message, t);
    }

    @Override
    public void warning(String msgFormat, Object... args) {
        if (msgFormat == null) {
            return;
        }
        String message = formatMessageIfNeeded(msgFormat, args);
        logger.w(TAG, message);
        throwExceptionIfProcessCrashed(message);
    }

    private void throwExceptionIfProcessCrashed(String message) {
        if (message.contains("Process crashed")) {
            throw new ProcessCrashedException("Process crashed");
        }
    }

    @Override
    public void info(String msgFormat, Object... args) {
        if (msgFormat == null) {
            return;
        }
        String msg = formatMessageIfNeeded(msgFormat, args);
        logger.i(TAG, msg);
    }

    private String formatMessageIfNeeded(String msgFormat, Object[] args) {
        String message;
        if (args.length == 0) {
            message = msgFormat;
        } else {
            message = String.format(msgFormat, args);
        }
        return message;
    }

    @Override
    public void verbose(String msgFormat, Object... args) {
        info(msgFormat, args);
    }
}

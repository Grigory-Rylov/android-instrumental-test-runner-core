package com.github.grishberg.tests.common;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * File logger.
 */
public class FileLogger implements RunnerLogger {
    private static final Logger sLogger = Logger.getLogger("LOG");
    private static final String FORMAT_STR = "%s: %s";
    private static final String DEBUG_FORMAT = "D/%s: %s";

    public FileLogger(Project project, String logName) {
        FileHandler fh;
        try {
            if (!project.getBuildDir().exists()) {
                project.getBuildDir().mkdirs();
            }
            fh = new FileHandler(new File(project.getBuildDir(), logName).toString());
            Formatter formatter = new MyFormatter();
            fh.setFormatter(formatter);
            sLogger.addHandler(fh);
        } catch (IOException e) {
            project.getLogger().error("Error while setup FileHandler", e);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (!sLogger.isLoggable(Level.WARNING)) {
            return;
        }
        sLogger.warning(String.format(FORMAT_STR, tag, msg));
    }

    @Override
    public void w(String tag, String msgFormat, Object... args) {
        if (!sLogger.isLoggable(Level.WARNING)) {
            return;
        }
        sLogger.info(String.format(FORMAT_STR, tag, String.format(msgFormat, args)));
    }

    @Override
    public void i(String tag, String msg) {
        if (!sLogger.isLoggable(Level.INFO)) {
            return;
        }
        sLogger.info(String.format(FORMAT_STR, tag, msg));
    }

    @Override
    public void i(String tag, String msgFormat, Object... objects) {
        if (msgFormat == null || !sLogger.isLoggable(Level.INFO)) {
            return;
        }
        sLogger.info(String.format(FORMAT_STR, tag, String.format(msgFormat, objects)));
    }

    @Override
    public void d(String tag, String msg) {
        if (!sLogger.isLoggable(Level.INFO)) {
            return;
        }
        sLogger.log(Level.INFO, String.format(DEBUG_FORMAT, tag, msg));
    }

    @Override
    public void d(String tag, String msgFormat, Object... objects) {
        if (msgFormat == null || !sLogger.isLoggable(Level.INFO)) {
            return;
        }
        sLogger.log(Level.INFO, String.format(DEBUG_FORMAT, tag, String.format(msgFormat, objects)));
    }

    @Override
    public void e(String tag, String msg) {
        if (!sLogger.isLoggable(Level.INFO)) {
            return;
        }
        sLogger.log(Level.INFO, String.format(FORMAT_STR, tag, msg));
    }

    @Override
    public void e(String tag, String msg, Throwable throwable) {
        if (!sLogger.isLoggable(Level.INFO)) {
            return;
        }
        sLogger.log(Level.INFO, String.format(FORMAT_STR, tag, msg), throwable);
    }

    private static class MyFormatter extends Formatter {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss]");

        @Override
        public String format(LogRecord record) {
            Date date = new Date(record.getMillis());
            return dateFormat.format(date) + "\t"
                    + record.getMessage() + "\n";
        }
    }
}

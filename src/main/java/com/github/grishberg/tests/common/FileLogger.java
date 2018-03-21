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

    public FileLogger(Project project, String logName) {
        FileHandler fh;
        try {
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
        sLogger.warning(tag + ": " + msg);
    }

    @Override
    public void i(String tag, String msg) {
        sLogger.info(tag + ": " + msg);
    }

    @Override
    public void i(String tag, String msgFormat, Object... objects) {
        sLogger.info(tag + ": " + String.format(msgFormat, objects));
    }

    @Override
    public void d(String tag, String msg) {
        sLogger.log(Level.INFO, "D/" + tag + ": " + msg);
    }

    @Override
    public void d(String tag, String msgFormat, Object... objects) {
        sLogger.log(Level.INFO, "D/" + tag + ": " + String.format(msgFormat, objects));
    }

    @Override
    public void e(String tag, String msg) {
        sLogger.log(Level.INFO, tag + ": " + msg);
    }

    @Override
    public void e(String tag, String msg, Throwable throwable) {
        sLogger.log(Level.INFO, tag + ": " + msg, throwable);
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

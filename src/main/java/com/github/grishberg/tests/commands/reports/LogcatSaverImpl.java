package com.github.grishberg.tests.commands.reports;

import com.github.grishberg.tests.DeviceShellExecuter;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.common.RunnerLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Saves logcat to report/logcat.
 */
public class LogcatSaverImpl implements LogcatSaver {
    private static final String TAG = LogcatSaverImpl.class.getSimpleName();
    private static final String OUT_DIR = "logcat";
    private final DeviceShellExecuter device;
    private final RunnerLogger logger;
    private final File logcatDir;

    public LogcatSaverImpl(DeviceShellExecuter device, File reportsDir, RunnerLogger logger) {
        this.device = device;
        this.logger = logger;

        logcatDir = new File(reportsDir, OUT_DIR);
        if (!logcatDir.exists() && !logcatDir.mkdirs()) {
            logger.e(TAG, "LogcatSaverImpl: cant make dir " + logcatDir);
        }
    }

    @Override
    public void clearLogcat() {
        try {
            logger.i(TAG, "clearLogcat");
            device.executeShellCommand("logcat -c");
        } catch (CommandExecutionException e) {
            logger.e(TAG, "clearLogcat exception", e);
        }
    }

    @Override
    public void saveLogcat(String testName) {
        try {
            logger.i(TAG, "saveLogcat for {}", testName);
            String logcat = device.executeShellCommandAndReturnOutput("logcat -v threadtime -d");
            saveToFile(testName, logcat);
        } catch (CommandExecutionException e) {
            logger.e(TAG, "clearLogcat exception", e);
        }
    }

    private void saveToFile(String testName, String logcat) throws CommandExecutionException {
        File outFile = new File(logcatDir, String.format("%s-%s.log", device.getName(), testName));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            writer.write(logcat);
        } catch (Throwable e) {
            throw new CommandExecutionException(e);
        }
    }
}

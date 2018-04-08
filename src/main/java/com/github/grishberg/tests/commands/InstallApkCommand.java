package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.common.RunnerLogger;

import java.io.File;
import java.util.Locale;

/**
 * Installs apk file to current device.
 */
public class InstallApkCommand implements DeviceRunnerCommand {
    private static final String TAG = InstallApkCommand.class.getSimpleName();
    private final File apkFile;

    public InstallApkCommand(File apkFile) {
        this.apkFile = apkFile;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device, TestRunnerContext runnerContext)
            throws ExecuteCommandException {
        RunnerLogger logger = runnerContext.getLogger();
        DeviceCommandResult result = new DeviceCommandResult();
        Exception lastException = null;
        for (int i = 0; i < 3; i++) {
            try {
                String extraArgument = "";
                logger.i(TAG, "InstallApkCommand: install file {}", apkFile.getName());
                device.installPackage(apkFile.getAbsolutePath(), true, extraArgument);
                break;
            } catch (Exception e) {
                logger.e(TAG, "InstallApkCommand: ", e);
                lastException = e;
            }
        }
        if (lastException != null) {
            throw new ExecuteCommandException(String.format(Locale.US,
                    "Exception while install app apk on device [%s]",
                    device.getSerialNumber()), lastException);
        }
        return result;
    }
}

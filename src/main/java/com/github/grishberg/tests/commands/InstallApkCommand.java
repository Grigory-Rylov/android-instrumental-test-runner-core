package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.DeviceWrapper;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.Locale;

/**
 * Installs apk file to current device.
 */
public class InstallApkCommand implements DeviceCommand {
    private final Logger logger;
    private final File apkFile;

    public InstallApkCommand(Logger logger, File apkFile) {
        this.logger = logger;
        this.apkFile = apkFile;
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper device) throws ExecuteCommandException {
        DeviceCommandResult result = new DeviceCommandResult();
        Exception lastException = null;
        for (int i = 0; i < 3; i++) {
            try {
                String extraArgument = "";
                logger.info("InstallApkCommand: install file {}", apkFile.getName());
                device.installPackage(apkFile.getAbsolutePath(), true, extraArgument);
                break;
            } catch (Exception e) {
                logger.error("InstallApkCommand: ", e);
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

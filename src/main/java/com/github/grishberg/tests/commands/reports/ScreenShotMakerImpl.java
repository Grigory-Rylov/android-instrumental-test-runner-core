package com.github.grishberg.tests.commands.reports;

import com.github.grishberg.tests.DeviceShellExecuter;
import com.github.grishberg.tests.common.FileHelper;
import com.github.grishberg.tests.common.RunnerLogger;

import java.io.File;

/**
 * Builds screenshot and pulls it from device.
 */
public class ScreenShotMakerImpl implements ScreenShotMaker {
    private static final String TAG = ScreenShotMakerImpl.class.getSimpleName();
    private static final String SCREENSHOT_DIR = "screenshots";
    private final DeviceShellExecuter deviceWrapper;
    private RunnerLogger logger;
    private File reportsDir;

    public ScreenShotMakerImpl(File reportsDir,
                               DeviceShellExecuter deviceWrapper,
                               RunnerLogger logger) {
        this.reportsDir = reportsDir;
        this.deviceWrapper = deviceWrapper;
        this.logger = logger;
    }

    @Override
    public void makeScreenshot(String className, String testName) {
        File outFile = generateScreenshotFile(className, testName);
        try {
            File parentDir = outFile.getParentFile();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    logger.e(TAG, "makeScreenshot: cant make dir " + parentDir);
                    return;
                }
            }
            FileHelper.cleanFolder(parentDir);
            deviceWrapper.executeShellCommand("screencap -p /sdcard/fail_screen.png");
            deviceWrapper.pullFile("/sdcard/fail_screen.png", outFile.getAbsolutePath());
            deviceWrapper.executeShellCommand("rm /sdcard/fail_screen.png");
        } catch (Exception e) {
            logger.e(TAG, "makeScreenshot fail:", e);
        }
    }

    private File generateScreenshotFile(String className, String testName) {
        return new File(reportsDir, String.format("%s/%s-%s#%s.png",
                SCREENSHOT_DIR, deviceWrapper.getName(), className, testName));
    }
}

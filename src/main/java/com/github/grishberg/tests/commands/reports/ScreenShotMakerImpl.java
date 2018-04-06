package com.github.grishberg.tests.commands.reports;

import com.github.grishberg.tests.DeviceShellExecuter;
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
    private File screenshotDir;

    public ScreenShotMakerImpl(File reportsDir,
                               DeviceShellExecuter deviceWrapper,
                               RunnerLogger logger) {
        this.deviceWrapper = deviceWrapper;
        this.logger = logger;

        screenshotDir = new File(reportsDir, SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            if (!screenshotDir.mkdirs()) {
                logger.e(TAG, "ScreenShotMakerImpl: cant make dir " + screenshotDir);
            }
        }
    }

    @Override
    public void makeScreenshot(String className, String testName) {
        File outFile = generateScreenshotFile(className, testName);
        try {
            deviceWrapper.executeShellCommand("screencap -p /sdcard/fail_screen.png");
            deviceWrapper.pullFile("/sdcard/fail_screen.png", outFile.getAbsolutePath());
            deviceWrapper.executeShellCommand("rm /sdcard/fail_screen.png");
        } catch (Exception e) {
            logger.e(TAG, "makeScreenshot fail:", e);
        }
    }

    private File generateScreenshotFile(String className, String testName) {
        return new File(screenshotDir, String.format("%s-%s#%s.png",
                deviceWrapper.getName(), className, testName));
    }
}

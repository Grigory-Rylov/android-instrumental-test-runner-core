package com.github.grishberg.tests.commands.reports;

import com.github.grishberg.tests.DeviceShellExecuter;
import com.github.grishberg.tests.common.RunnerLogger;

import java.io.File;
import java.util.Map;

/**
 * Builds screenshot and pulls it from device.
 */
public class ScreenShotMakerImpl implements ScreenShotMaker {
    private static final String TAG = ScreenShotMakerImpl.class.getSimpleName();
    private static final String SCREENSHOT_DIR = "screenshots";
    private final DeviceShellExecuter deviceWrapper;
    private RunnerLogger logger;
    private File screenshotDir;
    private final Map<String, String> screenshotRelationMap;

    public ScreenShotMakerImpl(Map<String, String> screenshotRelationMap,
                               File reportsDir,
                               DeviceShellExecuter deviceWrapper,
                               RunnerLogger logger) {
        this.deviceWrapper = deviceWrapper;
        this.screenshotRelationMap = screenshotRelationMap;
        this.logger = logger;

        screenshotDir = new File(reportsDir, SCREENSHOT_DIR);
        if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
            logger.e(TAG, "ScreenShotMakerImpl: cant make dir " + screenshotDir);
        }
    }

    @Override
    public void makeScreenshot(String className, String testName) {
        File outFile = generateScreenshotFile(className, testName);
        screenshotRelationMap.put(String.format("%s#%s", className, testName),
                String.format("%s/%s", SCREENSHOT_DIR, generateScreenshotName(className, testName)));
        try {
            deviceWrapper.executeShellCommand("screencap -p /sdcard/fail_screen.png");
            deviceWrapper.pullFile("/sdcard/fail_screen.png", outFile.getAbsolutePath());
            deviceWrapper.executeShellCommand("rm /sdcard/fail_screen.png");
        } catch (Exception e) {
            logger.e(TAG, "makeScreenshot fail:", e);
        }
    }

    private File generateScreenshotFile(String className, String testName) {
        return new File(screenshotDir, generateScreenshotName(className, testName));
    }

    private String generateScreenshotName(String className, String testName) {
        return String.format("%s-%s-%s.png",
                deviceWrapper.getName(), className, testName);
    }
}

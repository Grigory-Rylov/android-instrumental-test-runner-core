package com.github.grishberg.tests.commands.reports;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.utils.ILogger;

import java.io.File;
import java.io.IOException;

/**
 * Xml report generator with prefix for single tests.
 */
public class TestXmlReportsGenerator extends CustomTestRunListener {
    private String deviceName;
    private String projectName;
    private String flavorName;
    private String testPrefix;
    private ScreenShotMaker screenShotMaker;

    public TestXmlReportsGenerator(String deviceName,
                                   String projectName,
                                   String flavorName,
                                   String testPrefix,
                                   ILogger logger,
                                   ScreenShotMaker screenShotMaker) {
        super(deviceName, projectName, flavorName, logger);
        this.deviceName = deviceName;
        this.projectName = projectName;
        this.flavorName = flavorName;
        this.testPrefix = testPrefix;
        this.screenShotMaker = screenShotMaker;
    }

    @Override
    protected File getResultFile(File reportDir) throws IOException {
        return new File(reportDir,
                "TEST-" + deviceName + "-" + projectName + "-" +
                        flavorName + testPrefix + ".xml");
    }

    @Override
    public void testFailed(TestIdentifier test, String trace) {
        super.testFailed(test, trace);
        screenShotMaker.makeScreenshot(test.getClassName(), test.getTestName());
    }
}

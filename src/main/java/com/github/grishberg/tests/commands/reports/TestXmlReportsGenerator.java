package com.github.grishberg.tests.commands.reports;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.utils.ILogger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Xml report generator with prefix for single tests.
 */
public class TestXmlReportsGenerator extends CustomTestRunListener {
    private final String deviceName;
    private final String projectName;
    private final String testPrefix;
    private final ScreenShotMaker screenShotMaker;
    private final LogcatSaver logcatSaver;

    public TestXmlReportsGenerator(String deviceName,
                                   String projectName,
                                   String flavorName,
                                   String testPrefix,
                                   ILogger logger,
                                   ScreenShotMaker screenShotMaker,
                                   LogcatSaver logcatSaver) {
        super(deviceName, projectName, flavorName, logger);
        this.deviceName = deviceName;
        this.projectName = projectName;
        this.testPrefix = testPrefix;
        this.screenShotMaker = screenShotMaker;
        this.logcatSaver = logcatSaver;
    }

    @Override
    protected File getResultFile(File reportDir) throws IOException {
        return new File(reportDir,
                "TEST-" + deviceName + "-" + projectName + "-" + testPrefix + ".xml");
    }

    @Override
    public void testFailed(TestIdentifier test, String trace) {
        super.testFailed(test, trace);
        screenShotMaker.makeScreenshot(test.getClassName(), test.getTestName());
    }

    @Override
    public void testRunEnded(long elapsedTime, Map<String, String> runMetrics) {
        super.testRunEnded(elapsedTime, runMetrics);
        logcatSaver.saveLogcat("logcat");
    }
}

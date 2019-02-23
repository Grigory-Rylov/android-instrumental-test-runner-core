package com.github.grishberg.tests.commands.reports;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.utils.ILogger;
import com.github.grishberg.tests.XmlReportGeneratorDelegate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
    private XmlReportGeneratorDelegate xmlReportDelegate;
    private TestIdentifier currentTest;

    public TestXmlReportsGenerator(String deviceName,
                                   String projectName,
                                   String flavorName,
                                   String testPrefix,
                                   ILogger logger,
                                   ScreenShotMaker screenShotMaker,
                                   LogcatSaver logcatSaver,
                                   XmlReportGeneratorDelegate xmlReportDelegate) {
        super(deviceName, projectName, flavorName, logger);
        this.deviceName = deviceName;
        this.projectName = projectName;
        this.testPrefix = testPrefix;
        this.screenShotMaker = screenShotMaker;
        this.logcatSaver = logcatSaver;
        this.xmlReportDelegate = xmlReportDelegate;
    }

    @Override
    protected File getResultFile(File reportDir) throws IOException {
        return new File(reportDir,
                "TEST-" + deviceName + "-" + projectName + "-" + testPrefix + ".xml");
    }

    @Override
    public void testStarted(TestIdentifier test) {
        super.testStarted(test);
        currentTest = test;
    }

    @Override
    public void testStarted(TestIdentifier test, long startTime) {
        super.testStarted(test, startTime);
        currentTest = test;
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

    public void failLastTest(String trace) {
        testFailed(currentTest, trace);
        super.testEnded(currentTest, 0, new HashMap<>());
    }

    /**
     * @return current executed test.
     */
    public TestIdentifier getCurrentTest() {
        return currentTest;
    }

    @Override
    protected Map<String, String> getPropertiesAttributes() {
        Map<String, String> propertiesAttributes =
                Maps.newLinkedHashMap(super.getPropertiesAttributes());
        propertiesAttributes.putAll(xmlReportDelegate.provideProperties());
        return ImmutableMap.copyOf(propertiesAttributes);
    }
}

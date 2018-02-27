package com.github.grishberg.tests.commands.reports;

import com.android.builder.internal.testing.CustomTestRunListener;
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

    public TestXmlReportsGenerator(String deviceName,
                                   String projectName,
                                   String flavorName,
                                   String testPrefix,
                                   ILogger logger) {
        super(deviceName, projectName, flavorName, logger);
        this.deviceName = deviceName;
        this.projectName = projectName;
        this.flavorName = flavorName;
        this.testPrefix = testPrefix;
    }

    @Override
    protected File getResultFile(File reportDir) throws IOException {
        return new File(reportDir,
                "TEST-" + deviceName + "-" + projectName + "-" +
                        flavorName + testPrefix + ".xml");
    }
}

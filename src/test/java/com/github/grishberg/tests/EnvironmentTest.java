package com.github.grishberg.tests;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by grishberg on 23.03.18.
 */
public class EnvironmentTest {
    @Test
    public void returnsFileThatGivenInConstructor() throws Exception {
        File resultDir = new File("/results");
        File reportsDir = new File("/reports");
        File coverageDir = new File("/coverage");

        Environment environment = new Environment(resultDir, reportsDir, coverageDir);

        Assert.assertEquals(resultDir, environment.getResultsDir());
        Assert.assertEquals(reportsDir, environment.getReportsDir());
        Assert.assertEquals(coverageDir, environment.getCoverageDir());
    }
}
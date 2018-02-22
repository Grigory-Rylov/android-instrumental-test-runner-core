package com.github.grishberg.tests.planner.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by grishberg on 29.10.17.
 */
public class InstrumentTestLogParserTest {
    @Test
    public void parseAmInstrumentOutput() throws Exception {
        String fileName = "am_instrument_output.txt";
        InstrumentTestLogParser parser = new InstrumentTestLogParser();

        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileName), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        parser.processNewLines(lines.toArray(new String[lines.size()]));

        Set<TestPlan> testInstances = parser.getTestInstances();
        Assert.assertEquals(6, testInstances.size());
        TestPlan[] testPlanArray = testInstances.toArray(new TestPlan[testInstances.size()]);
        TestPlan testWithFeature = testPlanArray[2];
        Assert.assertNotNull(testWithFeature.getFeatureParameter());
    }
}

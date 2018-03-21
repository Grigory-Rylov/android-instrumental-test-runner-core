package com.github.grishberg.tests.planner.parser;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

        List<TestPlanElement> testInstances = parser.getTestInstances();
        Assert.assertEquals(6, testInstances.size());
        TestPlanElement[] testPlanArray = testInstances.toArray(new TestPlanElement[testInstances.size()]);
        TestPlanElement testWithFeature = testPlanArray[3];
        Assert.assertNotNull(testWithFeature.getFeature());
    }
}

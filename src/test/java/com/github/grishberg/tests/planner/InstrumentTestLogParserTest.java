package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.InstrumentTestLogParser;
import com.github.grishberg.tests.planner.InstrumentTestLogParser.ParserLogger;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by grishberg on 29.10.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentTestLogParserTest {
    public static final String TEST_LINE = "test line";
    private InstrumentTestLogParser parser = new InstrumentTestLogParser();

    @Test
    public void parseAmInstrumentOutput() throws Exception {
        String fileName = "am_instrument_output.txt";

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

    @Test
    public void useLoggerFromSetter() {
        ParserLogger mockLogger = mock(ParserLogger.class);
        parser.setLogger(mockLogger);

        parser.processNewLines(new String[]{TEST_LINE});

        verify(mockLogger).logLine(TEST_LINE);
    }
}

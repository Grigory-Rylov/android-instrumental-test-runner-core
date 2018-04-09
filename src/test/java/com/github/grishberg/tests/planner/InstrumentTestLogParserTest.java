package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.InstrumentTestLogParser.ParserLogger;
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
    private static final String TEST_LINE = "test line";
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

    @Test
    public void parseFlags() {
        String[] lines = getLinesForTest();

        parser.processNewLines(lines);

        List<TestPlanElement> elements = parser.getTestInstances();
        TestPlanElement element = elements.get(0);
        List<String> annotations = element.getAnnotations();
        List<String> flags = element.getFlags();

        Assert.assertEquals(1, annotations.size());
        Assert.assertEquals(1, flags.size());
        Assert.assertEquals("com.github.grishberg.annotaions.Feature", annotations.get(0));
        Assert.assertEquals("flag1=value1", flags.get(0));
    }

    @Test(expected = ProcessCrashedException.class)
    public void parserAppCrash() {
        String[] lines = new String[]{"INSTRUMENTATION_RESULT: shortMsg=Process crashed.",
                "INSTRUMENTATION_CODE: 0"};

        parser.processNewLines(lines);
    }

    @Test(expected = ProcessCrashedException.class)
    public void parserAppCrashWithLongMsg() {
        String[] lines = new String[]{"INSTRUMENTATION_RESULT: shortMsg=java.lang.ClassNotFoundException",
                "INSTRUMENTATION_RESULT: longMsg=java.lang.ClassNotFoundException: Didn't find class \"com.dtmilano.android.uiautomatorhelper.UiAutomatorHelperTestRunner\" on path: DexPathList[[zip file \"/system/framework/android.test.runner.jar\", zip file \"/data/app/com.dtmilano.android.culebratester.test-1/base.apk\", zip file \"/data/app/com.dtmilano.android.culebratester-1/base.apk\"],nativeLibraryDirectories=[/data/app/com.dtmilano.android.culebratester.test-1/lib/arm, /data/app/com.dtmilano.android.culebratester-1/lib/arm, /vendor/lib, /system/lib]",
                "INSTRUMENTATION_CODE: 0"};
        parser.processNewLines(lines);
    }

    private static String[] getLinesForTest() {
        return new String[]{"INSTRUMENTATION_STATUS: id=AndroidJUnitRunner",
                "INSTRUMENTATION_STATUS: current=1",
                "INSTRUMENTATION_STATUS: class=com.github.grishberg.instrumentaltestwithtestgroupsordering.ExampleEspressoTest",
                "INSTRUMENTATION_STATUS: stream=\ncom.github.grishberg.instrumentaltestwithtestgroupsordering.ExampleEspressoTest:",
                "INSTRUMENTATION_STATUS: numtests=6",
                "INSTRUMENTATION_STATUS: test=espressoTest1",
                "INSTRUMENTATION_STATUS_CODE: 1",
                "INSTRUMENTATION_STATUS: feature=+feature1?param1=enabled&param2=disabled",
                "INSTRUMENTATION_STATUS: flags=flag1=value1",
                "INSTRUMENTATION_STATUS: annotations=com.github.grishberg.annotaions.Feature"
        };
    }
}

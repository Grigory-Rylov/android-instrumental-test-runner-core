package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.TestUtils;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grishberg on 29.10.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentTestLogParserTest {
    private static final String TEST_ELEMENT_WITHOUT_ANNOTATION = "TestPlanElement{methodName=" +
            "'ignoredTestTabletButton2', className='com.github.grishberg.instrumentaltestsample." +
            "TabletTest', type=METHOD, annotations=[]}";
    private static final String TEST_ELEMENT_WITH_ANNOTATION_1 = "TestPlanElement{methodName=" +
            "'testPhoneButton2', className='com.github.grishberg.instrumentaltestsample." +
            "MainActivityTest', type=METHOD, annotations=[org.junit.Test]}";
    private static final String TEST_ELEMENT_WITH_ANNOTATION_2 = "TestPlanElement{methodName=" +
            "'testPhoneButton', className='com.github.grishberg.instrumentaltestsample." +
            "MainActivityTest', type=METHOD, annotations=[org.junit.Test]}";
    private static final String TEST_ELEMENT_WITH_ANNOTATION_3 = "TestPlanElement{methodName=" +
            "'testTabletButton', className='com.github.grishberg.instrumentaltestsample." +
            "TabletTest', type=METHOD, annotations=[com.github.grishberg.instrumentaltestsample" +
            ".TabletOnly,org.junit.Test]}";
    @Mock
    RunnerLogger runnerLogger;

    private InstrumentTestLogParser parser;

    @Before
    public void setUp() {
        parser = new InstrumentTestLogParser(runnerLogger);
    }

    @Test
    public void annotationNameNotEmpty() {
        parser.processNewLines(getLinesForTest());

        TestPlanElement element = parser.getTestInstances().get(0);
        List<AnnotationInfo> annotations = element.getAnnotations();

        Assert.assertEquals("com.github.grishberg.annotations.Feature", annotations.get(0).getName());
    }

    @Test
    public void stringArgumentNotEmpty() {
        parser.processNewLines(getLinesForTest());

        TestPlanElement element = parser.getTestInstances().get(0);
        List<AnnotationInfo> annotations = element.getAnnotations();

        AnnotationMember annotationMember = annotations.get(0).getMembersMap().get("stringParam");

        Assert.assertEquals("someParam=someValue", annotationMember.getStrValue());
    }

    @Test
    public void intArgumentNotEmpty() {
        parser.processNewLines(getLinesForTest());

        TestPlanElement element = parser.getTestInstances().get(0);
        List<AnnotationInfo> annotations = element.getAnnotations();

        AnnotationMember annotationMember = annotations.get(0).getMembersMap().get("intParam");

        Assert.assertEquals(new Integer(777), annotationMember.getIntValue());
    }

    @Test
    public void intArrayArgumentNotEmpty() {
        parser.processNewLines(getLinesForTest());

        TestPlanElement element = parser.getTestInstances().get(0);
        List<AnnotationInfo> annotations = element.getAnnotations();

        AnnotationMember annotationMember = annotations.get(0).getMembersMap().get("intArray");

        ArrayList<Integer> intArray = new ArrayList<>();
        intArray.add(0);
        intArray.add(1);
        intArray.add(2);
        Assert.assertEquals(intArray, annotationMember.getIntArray());
    }

    @Test
    public void stingArrayArgumentNotEmpty() {
        parser.processNewLines(getLinesForTest());

        TestPlanElement element = parser.getTestInstances().get(0);
        List<AnnotationInfo> annotations = element.getAnnotations();

        AnnotationMember annotationMember = annotations.get(0).getMembersMap().get("strArray");

        ArrayList<String> strArray = new ArrayList<>();
        strArray.add("one");
        strArray.add("two");
        Assert.assertEquals(strArray, annotationMember.getStrArray());
    }

    @Test
    public void boolArgumentNotEmpty() {
        parser.processNewLines(getLinesForTest());

        TestPlanElement element = parser.getTestInstances().get(0);
        List<AnnotationInfo> annotations = element.getAnnotations();

        AnnotationMember annotationMember = annotations.get(0).getMembersMap().get("boolParam");

        Assert.assertEquals(Boolean.TRUE, annotationMember.getBoolValue());
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

    @Test
    public void parseAmInstrumentOutput() throws Exception {
        whenParsedSampleOut();

        List<TestPlanElement> testInstances = parser.getTestInstances();
        Assert.assertEquals(4, testInstances.size());
    }

    @Test
    public void parseAmInstrumentOutputAndCheckTestWithoutAnnotations() throws Exception {
        whenParsedSampleOut();

        List<TestPlanElement> testInstances = parser.getTestInstances();
        TestPlanElement testWithoutAnnotation = testInstances.get(3);
        Assert.assertEquals(TEST_ELEMENT_WITHOUT_ANNOTATION, testWithoutAnnotation.toString());
    }

    @Test
    public void annotationsReadCorrectlyTest() throws Exception {
        whenParsedSampleOut();

        List<TestPlanElement> testInstances = parser.getTestInstances();

        Assert.assertEquals(TEST_ELEMENT_WITH_ANNOTATION_1, testInstances.get(0).toString());
        Assert.assertEquals(TEST_ELEMENT_WITH_ANNOTATION_2, testInstances.get(1).toString());
        Assert.assertEquals(TEST_ELEMENT_WITH_ANNOTATION_3, testInstances.get(2).toString());
    }

    @Test(expected = InstrumentTestLogParserException.class)
    public void parseErrorState(){
        parser.processNewLines(linesWithWrongRunner());

        List<TestPlanElement> testInstances = parser.getTestInstances();
    }

    private void whenParsedSampleOut() throws Exception {
        String fileName = "for_test/am_instrument_output.txt";

        List<String> lines = TestUtils.readFile(fileName);
        for (String line : lines) {
            String[] singleLineArray = new String[]{line};
            parser.processNewLines(singleLineArray);
        }
    }

    private static String[] getLinesForTest() {
        return new String[]{"INSTRUMENTATION_STATUS: id=AndroidJUnitRunner",
                "INSTRUMENTATION_STATUS: current=1",
                "INSTRUMENTATION_STATUS: class=com.github.grishberg.instrumentaltestwithtestgroupsordering.ExampleEspressoTest",
                "INSTRUMENTATION_STATUS: stream=\ncom.github.grishberg.instrumentaltestwithtestgroupsordering.ExampleEspressoTest:",
                "INSTRUMENTATION_STATUS: numtests=6",
                "INSTRUMENTATION_STATUS: test=espressoTest1",
                "INSTRUMENTATION_STATUS_CODE: 1",
                "INSTRUMENTATION_STATUS: annotations=[" +
                        "{\"members\":[{\"intArray\":[0,1,2],\"name\":\"intArray\",\"valueType\":\"[I\"}," +
                        "{\"name\":\"intParam\",\"valueType\":\"int\",\"intValue\"=777}," +
                        "{\"name\":\"boolParam\",\"valueType\":\"boolean\",\"boolValue\"=true}," +
                        "{\"name\":\"strArray\",\"strArray\":[\"one\",\"two\"],\"valueType\":\"[Ljava.lang.String;\"}," +
                        "{\"name\":\"stringParam\",\"valueType\":\"java.lang.String\",\"strValue\":\"someParam=someValue\"}]," +
                        "\"name\":\"com.github.grishberg.annotations.Feature\"}]"
        };
    }


    private static String[] linesWithWrongRunner() {
        return new String[]{"onError: commandError=true message=INSTRUMENTATION_FAILED: ru.yandex.searchplugin.tests/androidx.test.runner.ThereIsWrongLauncher",
                "android.util.AndroidException: INSTRUMENTATION_FAILED: ru.yandex.searchplugin.tests/androidx.test.runner.ThereIsWrongLauncher",
                "at com.android.commands.am.Instrument.run(Instrument.java:486)",
                "at com.android.commands.am.Am.runInstrument(Am.java:194)",
                "at com.android.commands.am.Am.onRun(Am.java:80)",
                "at com.android.internal.os.BaseCommand.run(BaseCommand.java:54)",
                "at com.android.commands.am.Am.main(Am.java:50)",
                "at com.android.internal.os.RuntimeInit.nativeFinishInit(Native Method)",
                "at com.android.internal.os.RuntimeInit.main(RuntimeInit.java:340)",
                "INSTRUMENTATION_STATUS: Error=Unable to find instrumentation info for: ComponentInfo{ru.yandex.searchplugin.tests/androidx.test.runner.ThereIsWrongLauncher}",
                "INSTRUMENTATION_STATUS: id=ActivityManagerService",
                "INSTRUMENTATION_STATUS_CODE: -1"
        };
    }

}

package com.github.grishberg.tests.commands.reports;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.utils.ILogger;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.XmlReportGeneratorDelegate;
import com.github.grishberg.tests.commands.NoStartedTestException;
import com.yandex.tests.VerboseLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by grishberg on 03.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestXmlReportsGeneratorTest {
    private static final String TEST_NAME = "test1";
    private static final String TEST_CLASS = "com.test.TestClass";

    private final ILogger logger = spy(new RunTestLogger(new VerboseLogger()));

    @Mock
    ScreenShotMaker screenShotMaker;
    @Mock
    LogcatSaver logcatSaver;

    TestIdentifier testIdentifier = new TestIdentifier(TEST_CLASS, TEST_NAME);

    @Mock
    XmlReportGeneratorDelegate xmlReportDelegate;
    private TestXmlReportsGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new TestXmlReportsGenerator("DevName",
                "ProjectName",
                "FlavorName",
                "TestPrefix",
                null,
                logger, screenShotMaker, logcatSaver, xmlReportDelegate);
    }

    @Test
    public void getResultFile() throws Exception {
        File file = generator.getResultFile(new File("/report"));
        Assert.assertEquals(new File("/report/TEST-DevName-ProjectName-TestPrefix.xml"), file);
    }

    @Test
    public void makeScreenshotWhenTestFailed() {
        generator.testFailed(testIdentifier, "trace");

        Mockito.verify(screenShotMaker).makeScreenshot(TEST_CLASS, TEST_NAME);
        verify(logcatSaver, never()).saveLogcat(anyString());
        verify(logcatSaver, never()).clearLogcat();
    }

    @Test
    public void saveLogcatWhenAllTestEnded() {
        generator.testRunEnded(100, new HashMap<>());

        verify(logcatSaver).saveLogcat("logcat");
    }

    @Test
    public void failLastTestAfterCrash() throws Exception {
        generator.testStarted(testIdentifier);
        generator.failLastTest("TRACE_LOG");

        verify(logger).warning(
                argThat(argument -> argument.contains("FAILED")),
                eq("com.test.TestClass"),
                eq("test1"));
        verify(logger).warning(eq("TRACE_LOG"));

        verify(screenShotMaker).makeScreenshot(TEST_CLASS, TEST_NAME);
    }

    @Test(expected = NoStartedTestException.class)
    public void failLastTestAfterEarlyCrashBadCase() throws Exception {
        generator.failLastTest("TRACE_LOG");
    }

    @Test
    public void failLastTestAfterEarlyCrash() throws Exception {
        generator = new TestXmlReportsGenerator("DevName",
                "ProjectName",
                "FlavorName",
                "TestPrefix",
                testIdentifier,
                logger, screenShotMaker, logcatSaver, xmlReportDelegate);
        generator.failLastTest("TRACE_LOG");

        verify(logger).warning(
                argThat(argument -> argument.contains("FAILED")),
                eq("com.test.TestClass"),
                eq("test1"));
        verify(logger).warning(eq("TRACE_LOG"));

        verify(screenShotMaker).makeScreenshot(TEST_CLASS, TEST_NAME);
    }
}

package com.github.grishberg.tests.commands.reports;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.utils.ILogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 03.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestXmlReportsGeneratorTest {
    private static final String TEST_NAME = "test1";
    private static final String TEST_CLASS = "com.test.TestClass";
    @Mock
    ILogger logger;
    @Mock
    ScreenShotMaker screenShotMaker;
    @Mock
    TestIdentifier testIdentifier;

    private TestXmlReportsGenerator generator;

    @Before
    public void setUp() throws Exception {
        when(testIdentifier.getTestName()).thenReturn(TEST_NAME);
        when(testIdentifier.getClassName()).thenReturn(TEST_CLASS);
        generator = new TestXmlReportsGenerator("DevName",
                "ProjectName",
                "FlavorName",
                "TestPrefix",
                logger, screenShotMaker);

    }

    @Test
    public void getResultFile() throws Exception {
        File file = generator.getResultFile(new File("/report"));
        Assert.assertEquals(new File("/report/TEST-DevName-ProjectName-FlavorNameTestPrefix.xml"), file);
    }

    @Test
    public void makeScreenshotWhenTestFailed() {
        generator.testFailed(testIdentifier, "trace");

        Mockito.verify(screenShotMaker).makeScreenshot(TEST_CLASS, TEST_NAME);


    }
}
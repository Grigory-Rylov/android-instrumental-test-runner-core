package com.github.grishberg.tests.commands.reports;

import com.android.utils.ILogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

/**
 * Created by grishberg on 03.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestXmlReportsGeneratorTest {
    @Mock
    ILogger logger;

    @Test
    public void getResultFile() throws Exception {
        TestXmlReportsGenerator generator = new TestXmlReportsGenerator("DevName",
                "ProjectName",
                "FlavorName",
                "TestPrefix", logger);
        File file = generator.getResultFile(new File("/report"));
        Assert.assertEquals(new File("/report/TEST-DevName-ProjectName-FlavorNameTestPrefix.xml"), file);
    }
}
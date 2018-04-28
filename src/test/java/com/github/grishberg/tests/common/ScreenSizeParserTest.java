package com.github.grishberg.tests.common;

import com.github.grishberg.tests.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by grishberg on 28.04.18.
 */
public class ScreenSizeParserTest {
    @Test
    public void testParser() throws Exception{
        String fileName = "for_test/dumpsys_window.txt";

        String output = String.join("/n", TestUtils.readFile(fileName));

        int[] screenSize = ScreenSizeParser.parseScreenSize(output);
        Assert.assertEquals(2048, screenSize[0]);
        Assert.assertEquals(1440, screenSize[1]);
    }
}
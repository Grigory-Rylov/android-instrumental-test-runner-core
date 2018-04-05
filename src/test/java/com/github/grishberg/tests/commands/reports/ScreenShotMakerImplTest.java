package com.github.grishberg.tests.commands.reports;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 05.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ScreenShotMakerImplTest {
    private static final String REPORTS = "reports";
    private static final String SCREENSHOT_NAME = "test_device-com.test.TestClass#test1.png";
    private final Project project = ProjectBuilder.builder().build();
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    RunnerLogger logger;
    private ScreenShotMakerImpl screenShotMaker;
    private File reportsDir = new File(project.getBuildDir(), REPORTS);

    @Before
    public void setUp() {
        when(deviceWrapper.getName()).thenReturn("test_device");
        screenShotMaker = new ScreenShotMakerImpl(reportsDir, deviceWrapper, logger);
    }

    @Test
    public void makeScreenshot() throws Exception {
        File outputScreenshotFile = new File(reportsDir, "/screenshots/" + SCREENSHOT_NAME);

        screenShotMaker.makeScreenshot("com.test.TestClass", "test1");

        InOrder inOrder = inOrder(deviceWrapper);
        inOrder.verify(deviceWrapper).executeShellCommand("screencap -p /sdcard/fail_screen.png");

        inOrder.verify(deviceWrapper).pullFile("/sdcard/fail_screen.png",
                outputScreenshotFile.getAbsolutePath());
        inOrder.verify(deviceWrapper).executeShellCommand("rm /sdcard/fail_screen.png");
    }
}
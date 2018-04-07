package com.github.grishberg.tests.commands.reports;

import com.github.grishberg.tests.DeviceShellExecuter;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.verify;

/**
 * Created by grishberg on 07.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogcatSaverImplTest {
    @Mock
    DeviceShellExecuter device;
    @Mock
    RunnerLogger logger;
    private File reportsDir = new File("reports");
    private LogcatSaverImpl logcatSaver;

    @Before
    public void setUp() throws Exception {
        logcatSaver = new LogcatSaverImpl(device, reportsDir, logger);
    }

    @Test
    public void clearLogcat() throws Exception {
        logcatSaver.clearLogcat();

        verify(device).executeShellCommand("logcat -c");
    }

    @Test
    public void saveLogcat() throws Exception {
        logcatSaver.saveLogcat("test1");

        verify(device).executeShellCommandAndReturnOutput("logcat -v threadtime -d");
    }
}
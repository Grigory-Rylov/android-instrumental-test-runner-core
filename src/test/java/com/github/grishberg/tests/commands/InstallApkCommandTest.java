package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 31.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstallApkCommandTest {
    @Mock
    RunnerLogger logger;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    TestRunnerContext context;
    private File apkFile = new File("/apk");
    private InstallApkCommand command;

    @Before
    public void setUp() throws Exception {
        when(deviceWrapper.getLogger()).thenReturn(logger);
        command = new InstallApkCommand(apkFile);
    }

    @Test
    public void execute() throws Exception {
        command.execute(deviceWrapper, context);

        verify(logger).i(InstallApkCommand.class.getSimpleName(),
                "InstallApkCommand: install file {}", apkFile.getName());
        verify(deviceWrapper).installPackage(apkFile.getAbsolutePath(), true, "");
    }
}
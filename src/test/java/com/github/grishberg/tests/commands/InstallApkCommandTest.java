package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.verify;

/**
 * Created by grishberg on 31.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstallApkCommandTest {
    @Mock
    Logger logger;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    private File apkFile = new File("/apk");
    private InstallApkCommand command;

    @Before
    public void setUp() throws Exception {
        command = new InstallApkCommand(logger, apkFile);
    }

    @Test
    public void execute() throws Exception {
        command.execute(deviceWrapper);

        verify(logger).info("InstallApkCommand: install file {}", apkFile.getName());
        verify(deviceWrapper).installPackage(apkFile.getAbsolutePath(), true, "");
    }
}
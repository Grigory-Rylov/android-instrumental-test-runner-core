package com.github.grishberg.tests;

import com.android.ddmlib.IDevice;
import com.github.grishberg.tests.adb.AdbWrapper;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 28.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentationTestTaskTest {
    private static final String TEST_DIR = "/test_dir";
    public static final String ADB_PATH = "/adb_path";
    private final Project project = ProjectBuilder.builder().build();
    private InstrumentationTestTask task;
    private InstrumentalPluginExtension ext;
    @Mock
    AdbWrapper adbWrapper;
    @Mock
    DeviceCommandsRunnerFabric deviceCommandsRunnerFabric;
    @Mock
    RunnerLogger logger;
    @Mock
    IDevice device;
    @Mock
    DeviceCommandsRunner runner;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;

    public InstrumentationTestTaskTest() {
        project.getPluginManager().apply(com.github.grishberg.tests.InstrumentalTestPlugin.class);
        ext = project.getExtensions().findByType(InstrumentalPluginExtension.class);
        task = provideTask();
        ext.setAndroidSdkPath(ADB_PATH);
    }

    @Before
    public void setUp() throws Exception {
        IDevice[] devices = new IDevice[]{device};
        when((adbWrapper.provideDevices())).thenReturn(devices);
        when(deviceCommandsRunnerFabric.provideDeviceCommandRunner(any(DeviceRunnerCommandProvider.class),
                any(Environment.class))).thenReturn(runner);
        when(runner.runCommands(any(ConnectedDeviceWrapper[].class))).thenReturn(true);
        task.initAfterApply(adbWrapper, deviceCommandsRunnerFabric, logger);
    }

    private InstrumentationTestTask provideTask() {
        return (InstrumentationTestTask) project.getTasks().getByName(InstrumentationTestTask.NAME);
    }

    @Test
    public void getCoverageDirWhenNotInitiated() throws Exception {
        File dir = task.getCoverageDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/coverage/default_flavor"));
    }

    @Test
    public void getCoverageDirWhenInitiated() throws Exception {
        task.setCoverageDir(new File(TEST_DIR));

        File dir = task.getCoverageDir();

        Assert.assertEquals(TEST_DIR, dir.getAbsolutePath());
    }

    @Test
    public void getCoverageDirWhenExtensionInitiated() throws Exception {
        ext.setFlavorName("custom_flavor");
        task = provideTask();

        File dir = task.getCoverageDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/coverage/custom_flavor"));
    }

    @Test
    public void getResultsDir() throws Exception {
        File dir = task.getResultsDir();
        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/default_flavor"));
    }

    @Test
    public void getResultsDirWhenInitiated() throws Exception {
        task.setResultsDir(new File(TEST_DIR));

        File dir = task.getResultsDir();

        Assert.assertEquals(TEST_DIR, dir.getAbsolutePath());
    }

    @Test
    public void getResultsDirWhenExtensionInitiated() throws Exception {
        ext.setFlavorName("custom_flavor");
        task = provideTask();

        File dir = task.getResultsDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/custom_flavor"));
    }

    @Test
    public void getReportsDir() throws Exception {
        File dir = task.getReportsDir();
        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/default_flavor"));
    }

    @Test
    public void getReportsDirWhenInitiated() throws Exception {
        task.setReportsDir(new File(TEST_DIR));
        File dir = task.getReportsDir();

        Assert.assertEquals(TEST_DIR, dir.getAbsolutePath());
    }

    @Test
    public void getReportsDirWhenExtensionInitiated() throws Exception {
        ext.setFlavorName("custom_flavor");
        task = provideTask();

        File dir = task.getReportsDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/custom_flavor"));
    }

    @Test
    public void runTest() throws Exception {
        task.runTask();

        verify(adbWrapper).initWithAndroidSdk(ADB_PATH);
        verify(adbWrapper).waitForAdb();
        verify(deviceCommandsRunnerFabric).provideDeviceCommandRunner(any(DeviceRunnerCommandProvider.class),
                any(Environment.class));

    }
}
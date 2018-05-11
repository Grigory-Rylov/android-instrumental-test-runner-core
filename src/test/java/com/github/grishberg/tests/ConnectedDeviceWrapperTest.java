package com.github.grishberg.tests;

import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.TimeoutException;
import com.android.utils.ILogger;
import com.github.grishberg.tests.commands.ExecuteCommandException;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by grishberg on 22.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConnectedDeviceWrapperTest {
    @Mock
    IDevice device;
    @Mock
    IShellOutputReceiver shellOutputReceiver;
    @Mock
    InstrumentalPluginExtension extension;
    @Mock
    ILogger logger;
    @Mock
    RunnerLogger runnerLogger;
    private ConnectedDeviceWrapper deviceWrapper;
    private File coverageFile = new File("coverage");

    @Before
    public void setUp() throws Exception {
        when(device.getName()).thenReturn("test_device");
        when(extension.getApplicationId()).thenReturn("com.test.app");
        deviceWrapper = new ConnectedDeviceWrapper(device, runnerLogger);
    }

    @Test
    public void executeShellCommand() throws Exception {
        deviceWrapper.executeShellCommand("cmd", shellOutputReceiver, 0L, TimeUnit.MILLISECONDS);
        verify(device).executeShellCommand("cmd", shellOutputReceiver, 0L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void getSystemProperty() throws Exception {
        deviceWrapper.getSystemProperty("propertyName");
        verify(device).getSystemProperty("propertyName");
    }

    @Test
    public void getName() throws Exception {
        deviceWrapper.getName();
        verify(device).getName();
    }

    @Test
    public void pullFile() throws Exception {
        deviceWrapper.pullFile("temporaryCoverageCopy", "path");
        verify(device).pullFile("temporaryCoverageCopy", "path");
    }

    @Test
    public void isEmulator() throws Exception {
        deviceWrapper.isEmulator();
        verify(device).isEmulator();
    }

    @Test
    public void getSerialNumber() throws Exception {
        deviceWrapper.getSerialNumber();
        verify(device).getSerialNumber();
    }

    @Test
    public void installPackage() throws Exception {
        deviceWrapper.installPackage("absolutePath", true, "extraArgument");
        verify(device).installPackage("absolutePath", true, "extraArgument");
    }

    @Test
    public void pullCoverageFile() throws Exception {
        deviceWrapper.pullCoverageFile(extension, "coverageFilePrefix", "coverageFile",
                coverageFile, logger);
        verify(logger).verbose(anyString(), anyString(), anyString());
    }

    @Test
    public void executeShellCommand1() throws Exception {
        deviceWrapper.executeShellCommand("cmd", shellOutputReceiver, 0L, 0L,
                TimeUnit.MILLISECONDS);
        verify(device).executeShellCommand("cmd", shellOutputReceiver, 0L, 0L,
                TimeUnit.MILLISECONDS);
    }

    @Test
    public void executeShellCommand2() throws Exception {
        deviceWrapper.executeShellCommand("cmd");
        verify(device).executeShellCommand(eq("cmd"), any(CollectingOutputReceiver.class),
                eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    public void executeShellCommandAndReturnOutput() throws Exception {
        String result = deviceWrapper.executeShellCommandAndReturnOutput("cmd");
        verify(device).executeShellCommand(eq("cmd"), any(CollectingOutputReceiver.class),
                eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    public void returnDeviceWhenAsked() {
        Assert.assertEquals(device, deviceWrapper.getDevice());
    }

    @Test(expected = ExecuteCommandException.class)
    public void throwExecuteCommandExceptionWhenPullFileAndOtherException() throws Exception {
        doThrow(new TimeoutException()).when(device)
                .pullFile(anyString(), anyString());

        deviceWrapper.pullFile("coverageCopy", "path");
    }
}
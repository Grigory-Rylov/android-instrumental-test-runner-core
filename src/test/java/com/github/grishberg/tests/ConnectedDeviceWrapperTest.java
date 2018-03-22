package com.github.grishberg.tests;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.utils.ILogger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

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
    File coverageFile;
    @Mock
    ILogger logger;
    private ConnectedDeviceWrapper deviceWrapper;

    @Before
    public void setUp() throws Exception {
        deviceWrapper = new ConnectedDeviceWrapper(device);
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
        verify(device).getAvdName();
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
    @Ignore
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
}
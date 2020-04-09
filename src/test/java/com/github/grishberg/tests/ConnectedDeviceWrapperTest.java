package com.github.grishberg.tests;

import com.android.ddmlib.CollectingOutputReceiver;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.TimeoutException;
import com.android.utils.ILogger;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyLong;
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
    InstrumentalExtension extension;
    @Mock
    ILogger logger;
    @Mock
    RunnerLogger runnerLogger;
    private IShellOutputReceiver receiver;
    private ConnectedDeviceWrapper deviceWrapper;
    private File coverageFile = new File("coverage");

    @Before
    public void setUp() throws Exception {
        when(device.getName()).thenReturn("test_device");
        when(extension.getApplicationId()).thenReturn("com.test.app");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                receiver = invocation.getArgument(1);
                byte[] bytes = getDumpsysContent().getBytes();
                receiver.addOutput(bytes, 0, bytes.length);
                return null;
            }
        }).when(device).executeShellCommand(eq("dumpsys window"),
                any(IShellOutputReceiver.class),
                anyLong(),
                any(TimeUnit.class));
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
                coverageFile, runnerLogger);
        verify(runnerLogger).i(anyString(), anyString(), anyString());
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
        assertEquals(device, deviceWrapper.getDevice());
    }

    @Test(expected = CommandExecutionException.class)
    public void throwExecuteCommandExceptionWhenPullFileAndOtherException() throws Exception {
        doThrow(new TimeoutException()).when(device)
                .pullFile(anyString(), anyString());

        deviceWrapper.pullFile("coverageCopy", "path");
    }

    @Test
    public void testGetDensity() {
        deviceWrapper.getDensity();
        verify(device).getDensity();
    }

    @Test
    public void testGetWidth() {
        assertEquals(2048, deviceWrapper.getWidth());
    }

    @Test
    public void testGetHeight() {
        assertEquals(1440, deviceWrapper.getHeight());
    }

    @Test
    public void testGetWidthInDp() {
        when(device.getDensity()).thenReturn(320);
        assertEquals(1024, deviceWrapper.getWidthInDp());
    }

    @Test
    public void testGetHeightInDp() {
        when(device.getDensity()).thenReturn(320);
        assertEquals(720, deviceWrapper.getHeightInDp());
    }

    @Test
    public void devicesAreEqualsWhenSerialNumbersAreEquals() {
        ConnectedDeviceWrapper otherDevice = mock(ConnectedDeviceWrapper.class);
        when(otherDevice.getSerialNumber()).thenReturn("123");
        when(device.getSerialNumber()).thenReturn("123");
        assertTrue(deviceWrapper.equals(otherDevice));
    }

    @Test
    public void devicesAreEqualsWhenSerialNumberIsEmptyAndNamesAreEquals() {
        ConnectedDeviceWrapper otherDevice = mock(ConnectedDeviceWrapper.class);
        when(otherDevice.getName()).thenReturn("abc");
        when(device.getName()).thenReturn("abc");
        assertTrue(deviceWrapper.equals(otherDevice));
    }

    private String getDumpsysContent() throws Exception {
        String fileName = "for_test/dumpsys_window.txt";
        return String.join("/n", TestUtils.readFile(fileName));
    }
}
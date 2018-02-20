package com.github.grishberg.tests;

import com.android.ddmlib.*;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Wraps {@link IDevice} interface.
 */
public class DeviceWrapper implements IShellEnabledDevice {
    private final IDevice device;
    private String name;

    public DeviceWrapper(IDevice device) {
        this.device = device;
    }

    @Override
    public void executeShellCommand(String command,
                                    IShellOutputReceiver receiver,
                                    long maxTimeToOutputResponse,
                                    TimeUnit maxTimeUnits) throws TimeoutException,
            AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        device.executeShellCommand(command, receiver, maxTimeToOutputResponse, maxTimeUnits);
    }

    @Override
    public Future<String> getSystemProperty(String name) {
        return device.getSystemProperty(name);
    }

    public String getName() {
        if (name == null) {
            //TODO: format device name from api level
            name = device.getAvdName();
        }
        return name;
    }

    public IDevice getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "DeviceWrapper{" +
                "sn=" + device.getSerialNumber() +
                ", isOnline=" + device.isOnline() +
                ", name='" + getName() + '\'' +
                '}';
    }

    public void pullFile(String temporaryCoverageCopy, String path) throws TimeoutException,
            AdbCommandRejectedException, SyncException, IOException {
        device.pullFile(temporaryCoverageCopy, path);
    }

    public boolean isEmulator() {
        return device.isEmulator();
    }

    public String getSerialNumber() {
        return device.getSerialNumber();
    }
}

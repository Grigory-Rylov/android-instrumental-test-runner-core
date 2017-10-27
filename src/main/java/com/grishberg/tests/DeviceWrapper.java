package com.grishberg.tests;

import com.android.ddmlib.*;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by grishberg on 19.10.17.
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
}

package com.github.grishberg.tests.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.common.RunnerLogger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps interaction with adb static methods.
 */
public class AdbWrapper {
    private static final int ADB_TIMEOUT = 10;
    private static final int ONE_SECOND = 1000;
    @Nullable
    private AndroidDebugBridge adb;
    private RunnerLogger logger = new RunnerLogger.Stub();

    public synchronized void init(String androidSdkPath, RunnerLogger logger) {
        adb = AndroidDebugBridge.createBridge(androidSdkPath + "/platform-tools/adb", false);
        this.logger = logger;
    }

    public synchronized void waitForAdb() throws InterruptedException {
        if (adb == null) {
            throw new IllegalStateException("Need to call init() first");
        }

        for (int counter = 0; counter < ADB_TIMEOUT; counter++) {
            if (adb.isConnected()) {
                break;
            }
            Thread.sleep(ONE_SECOND);
        }
    }

    /**
     * @return list of available android devices.
     */
    public synchronized List<ConnectedDeviceWrapper> provideDevices() {
        if (adb == null) {
            throw new IllegalStateException("Need to call init() first");
        }
        IDevice[] devices = adb.getDevices();
        ArrayList<ConnectedDeviceWrapper> deviceWrappers = new ArrayList<>(devices.length);
        for (IDevice device : devices) {
            deviceWrappers.add(new ConnectedDeviceWrapper(device, logger));
        }
        return deviceWrappers;
    }
}

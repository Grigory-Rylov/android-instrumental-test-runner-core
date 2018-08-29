package com.github.grishberg.tests.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Nullable;

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

    public void init(String androidSdkPath, RunnerLogger logger) {
        adb = AndroidDebugBridge.createBridge(androidSdkPath + "/platform-tools/adb", false);
        this.logger = logger;
    }

    public void waitForAdb() throws InterruptedException {
        for (int counter = 0; counter < ADB_TIMEOUT; counter++) {
            if (adb.isConnected()) {
                break;
            }
            Thread.sleep(ONE_SECOND);
        }
    }

    public List<ConnectedDeviceWrapper> provideDevices() {
        IDevice[] devices = adb.getDevices();
        ArrayList<ConnectedDeviceWrapper> deviceWrappers = new ArrayList<>(devices.length);
        for (int i = 0; i < devices.length; i++) {
            deviceWrappers.add(new ConnectedDeviceWrapper(devices[i], logger));
        }
        return deviceWrappers;
    }
}

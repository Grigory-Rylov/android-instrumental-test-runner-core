package com.github.grishberg.tests.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import org.gradle.api.Nullable;

/**
 * Wraps interaction with adb static methods.
 */
public class AdbWrapper {
    private static final int ADB_TIMEOUT = 10;
    private static final int ONE_SECOND = 1000;
    @Nullable
    private AndroidDebugBridge adb;

    public void initWithAndroidSdk(String androidSdkPath) {
        adb = AndroidDebugBridge.createBridge(androidSdkPath + "/platform-tools/adb", false);
    }

    public void waitForAdb() throws InterruptedException {
        for (int counter = 0; counter < ADB_TIMEOUT; counter++) {
            if (adb.isConnected()) {
                break;
            }
            Thread.sleep(ONE_SECOND);
        }
    }

    public IDevice[] provideDevices() {
        return adb.getDevices();
    }
}

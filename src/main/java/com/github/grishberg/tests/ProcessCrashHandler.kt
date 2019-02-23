package com.github.grishberg.tests

import com.android.ddmlib.testrunner.TestIdentifier

/**
 * Handles process crashed event.
 */
interface ProcessCrashHandler {
    /**
     * Is called when process crashed.
     * returns fail message for xml report.
     */
    fun provideFailMessageOnProcessCrashed(targetDevice: ConnectedDeviceWrapper, failedTest: TestIdentifier): String

    object STUB : ProcessCrashHandler {
        override fun provideFailMessageOnProcessCrashed(device: ConnectedDeviceWrapper,
                                                        failedTest: TestIdentifier) =
                "Process was crashed. See logcat for details."
    }
}
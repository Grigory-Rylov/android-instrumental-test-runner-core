package com.github.grishberg.tests.sharding

import com.github.grishberg.tests.ConnectedDeviceWrapper

interface DeviceTypeAdapter {
    /**
     * Provides device type.
     */
    fun provideDeviceType(deviceWrapper: ConnectedDeviceWrapper): Int
}
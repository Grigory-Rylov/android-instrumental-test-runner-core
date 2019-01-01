package com.github.grishberg.tests.sharding

import com.github.grishberg.tests.ConnectedDeviceWrapper

/**
 * Default device adapter for single device type.
 */
class DefaultDeviceTypeAdapter : DeviceTypeAdapter {
    override fun provideDeviceType(deviceWrapper: ConnectedDeviceWrapper): Int = 0
}
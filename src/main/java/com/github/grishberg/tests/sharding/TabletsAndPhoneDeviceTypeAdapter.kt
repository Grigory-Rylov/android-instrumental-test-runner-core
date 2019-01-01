package com.github.grishberg.tests.sharding

import com.github.grishberg.tests.ConnectedDeviceWrapper

private const val TYPE_PHONE = 0
private const val TYPE_TABLET = 1

/**
 * Provides shard info for phone devices and tablet devices.
 */
class TabletsAndPhoneDeviceTypeAdapter(
        private val tabletMinWidthInDp: Int = 600
) : DeviceTypeAdapter {
    override fun provideDeviceType(deviceWrapper: ConnectedDeviceWrapper): Int =
            if (deviceWrapper.widthInDp < tabletMinWidthInDp) TYPE_PHONE else TYPE_TABLET
}
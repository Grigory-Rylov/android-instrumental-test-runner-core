package com.github.grishberg.tests.sharding

import com.github.grishberg.tests.ConnectedDeviceWrapper

/**
 * generates shard arguments for devices.
 */
interface ShardArguments {
    /**
     * Adds shard arguments to am instrument args for current device.
     */
    fun createShardArguments(currentDevice: ConnectedDeviceWrapper): Map<String, String>
}

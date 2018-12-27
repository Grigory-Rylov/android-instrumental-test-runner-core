package com.github.grishberg.tests

import com.github.grishberg.tests.adb.AdbWrapper
import com.github.grishberg.tests.common.RunnerLogger

private const val NUM_SHARDS_PARAM = "numShards"
private const val SHARD_INDEX_PARAM = "shardIndex"
private const val TAG = "AbsShardingArguments"

interface ShardingArguments {
    /**
     * Adds shard arguments to am instrument args for current device.
     */
    fun addShardingArguments(currentDevice: ConnectedDeviceWrapper, args: HashMap<String, String>)

    /**
     * Provides device type.
     */
    fun provideDeviceType(deviceWrapper: ConnectedDeviceWrapper): Int
}

/**
 * generates shard arguments for devices.
 */
abstract class AbsShardingArguments(
        private val adbWrapper: AdbWrapper,
        private val logger: RunnerLogger
) : ShardingArguments {
    private val devicesByTypeMap = HashMap<Int, ArrayList<ConnectedDeviceWrapper>>()

    /**
     * Adds shard arguments to am instrument args for current device.
     */
    override fun addShardingArguments(currentDevice: ConnectedDeviceWrapper, args: HashMap<String, String>) {
        if (devicesByTypeMap.size == 0) {
            populateDeviceMap()
        }

        val deviceType = provideDeviceType(currentDevice)
        val numShards = devicesByTypeMap[deviceType]!!.size
        val shardIndex = getDeviceIndex(currentDevice, devicesByTypeMap[deviceType]!!)

        args[NUM_SHARDS_PARAM] = "$numShards"
        args[SHARD_INDEX_PARAM] = "$shardIndex"
    }

    private fun getDeviceIndex(currentDevice: ConnectedDeviceWrapper,
                               deviceList: ArrayList<ConnectedDeviceWrapper>): Int {
        for (index in 0 until deviceList.size) {
            if (deviceList[index] == currentDevice) {
                return index
            }
        }

        logger.e(TAG, "device $currentDevice not found")
        return 0
    }

    private fun populateDeviceMap() {
        val devices = adbWrapper.provideDevices()
        devices.forEach {
            val currentDeviceType = provideDeviceType(it)
            val currentList = devicesByTypeMap.getOrDefault(currentDeviceType, ArrayList())
            currentList.add(it)
        }
    }
}
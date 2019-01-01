package com.github.grishberg.tests.sharding

import com.github.grishberg.tests.ConnectedDeviceWrapper
import com.github.grishberg.tests.adb.AdbWrapper
import com.github.grishberg.tests.common.RunnerLogger

private const val NUM_SHARDS_PARAM = "numShards"
private const val SHARD_INDEX_PARAM = "shardIndex"
private const val TAG = "AbsShardingArguments"

/**
 * generates shard arguments for devices.
 */
class ShardArgumentsImpl(
        private val adbWrapper: AdbWrapper,
        private val logger: RunnerLogger,
        private val deviceTypeAdapter: DeviceTypeAdapter
) : ShardArguments {
    private val devicesByTypeMap = HashMap<Int, ArrayList<ConnectedDeviceWrapper>>()

    /**
     * Adds shard arguments to am instrument args for current device.
     */
    override fun createShardArguments(currentDevice: ConnectedDeviceWrapper): Map<String, String> {
        if (devicesByTypeMap.size == 0) {
            populateDeviceMap()
        }

        val deviceType = deviceTypeAdapter.provideDeviceType(currentDevice)
        val numShards = devicesByTypeMap[deviceType]!!.size
        val shardIndex = getDeviceIndex(currentDevice, devicesByTypeMap[deviceType]!!)

        val args = HashMap<String, String>()
        args[NUM_SHARDS_PARAM] = "$numShards"
        args[SHARD_INDEX_PARAM] = "$shardIndex"
        return args
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
            val currentDeviceType = deviceTypeAdapter.provideDeviceType(it)
            val currentList = devicesByTypeMap.getOrDefault(currentDeviceType, ArrayList())
            devicesByTypeMap.putIfAbsent(currentDeviceType, currentList)
            currentList.add(it)
        }
    }
}
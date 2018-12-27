package com.github.grishberg.tests

import com.github.grishberg.tests.adb.AdbWrapper
import com.github.grishberg.tests.common.RunnerLogger

/**
 * Default arguments for single device type.
 */
class DefaultShardingArguments(
        private val adbWrapper: AdbWrapper,
        private val logger: RunnerLogger
) : AbsShardingArguments(adbWrapper, logger) {
    override fun provideDeviceType(deviceWrapper: ConnectedDeviceWrapper): Int = 0
}
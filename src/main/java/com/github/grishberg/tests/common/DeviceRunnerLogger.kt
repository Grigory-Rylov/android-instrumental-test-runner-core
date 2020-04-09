package com.github.grishberg.tests.common

import com.github.grishberg.tests.ConnectedDeviceWrapper

/**
 * {@link RunnerLogger} wrapper with device name as TAG postfix.
 */
class DeviceRunnerLogger(
        private val device: ConnectedDeviceWrapper,
        private val logger: RunnerLogger
) : RunnerLogger {
    override fun w(tag: String, message: String) {
        logger.w(buildTagWithDevice(tag), message)
    }

    override fun i(tag: String, message: String) {
        logger.i(buildTagWithDevice(tag), message)
    }

    override fun i(tag: String, msgFormat: String, vararg args: Any?) {
        logger.i(buildTagWithDevice(tag), msgFormat, args)
    }

    override fun d(tag: String, message: String?) {
        logger.d(buildTagWithDevice(tag), message)
    }

    override fun d(tag: String, msgFormat: String, vararg args: Any?) {
        logger.d(buildTagWithDevice(tag), msgFormat, args)
    }

    override fun e(tag: String, message: String?) {
        logger.e(buildTagWithDevice(tag), message)
    }

    override fun e(tag: String, message: String?, throwable: Throwable?) {
        logger.e(buildTagWithDevice(tag), message, throwable)
    }

    override fun w(tag: String, msgFormat: String, vararg args: Any?) {
        logger.w(buildTagWithDevice(tag), msgFormat, args)
    }

    private fun buildTagWithDevice(tag: String) = "${device.name} / $tag"
}
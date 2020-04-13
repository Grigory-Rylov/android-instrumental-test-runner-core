package com.github.grishberg.tests.common

import com.android.ddmlib.IDevice

/**
 * [RunnerLogger] wrapper with device name as TAG prefix.
 */
class DeviceSpecificLogger(
        private val device: IDevice,
        private val logger: RunnerLogger) : RunnerLogger {
    override fun w(tag: String, message: String) {
        logger.w(createTag(tag), message)
    }

    override fun i(tag: String, message: String) {
        logger.i(createTag(tag), message)
    }

    override fun i(tag: String, msgFormat: String, vararg args: Any?) {
        logger.i(createTag(tag), msgFormat, args)
    }

    override fun d(tag: String, message: String?) {
        logger.d(createTag(tag), message);
    }

    override fun d(tag: String, msgFormat: String, vararg args: Any?) {
        logger.d(createTag(tag), msgFormat, args)
    }

    override fun e(tag: String, message: String?) {
        logger.e(createTag(tag), message);
    }

    override fun e(tag: String, message: String?, throwable: Throwable?) {
        logger.e(createTag(tag), message, throwable);
    }

    override fun w(tag: String, msgFormat: String, vararg args: Any?) {
        logger.w(createTag(tag), msgFormat, args)
    }

    private fun createTag(tag: String): String = "${device.name} / $tag"
}

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
        logger.w("$tag/${device.name}", message)
    }

    override fun i(tag: String, message: String) {
        logger.i("$tag/${device.name}", message)
    }

    override fun i(tag: String, msgFormat: String, vararg args: Any?) {
        logger.i("$tag/${device.name}", msgFormat, args)
    }

    override fun d(tag: String, message: String?) {
        logger.d("$tag/${device.name}", message);
    }

    override fun d(tag: String, msgFormat: String, vararg args: Any?) {
        logger.d("$tag/${device.name}", msgFormat, args)
    }

    override fun e(tag: String, message: String?) {
        logger.e("$tag/${device.name}", message);
    }

    override fun e(tag: String, message: String?, throwable: Throwable?) {
        logger.e("$tag/${device.name}", message, throwable);
    }

    override fun w(tag: String, msgFormat: String, vararg args: Any?) {
        logger.w("$tag/${device.name}", msgFormat, args)
    }
}
package com.github.grishberg.tests.common

import org.gradle.api.logging.Logger

/**
 * Default RunnerLogger implementation.
 */
class DefaultGradleLogger(private val logger: Logger) : RunnerLogger {

    override fun w(tag: String, message: String?) {
        logger.warn(FORMAT_STR, tag, message)
    }

    override fun w(tag: String, msgFormat: String, vararg args: Any) {
        if (logger.isWarnEnabled) {
            logger.warn("$tag$TAG_DIVIDER$msgFormat", *args)
        }
    }

    override fun i(tag: String, message: String?) {
        if (logger.isInfoEnabled) {
            logger.info(FORMAT_STR, tag, message)
        }
    }

    override fun i(tag: String, msgFormat: String?, vararg args: Any) {
        if (msgFormat == null || !logger.isInfoEnabled) {
            return
        }
        logger.info("$tag$TAG_DIVIDER$msgFormat", *args)
    }

    override fun d(tag: String, message: String?) {
        logger.debug(FORMAT_STR, tag, message)
    }

    override fun d(tag: String, msgFormat: String?, vararg args: Any) {
        if (msgFormat == null || !logger.isDebugEnabled) {
            return
        }
        logger.debug("$tag$TAG_DIVIDER$msgFormat", *args)
    }

    override fun e(tag: String, message: String?) {
        logger.error(FORMAT_STR, tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        logger.error(String.format("%s: %s", tag, message), throwable)
    }

    companion object {
        private val FORMAT_STR = "{}: {}"
        private val TAG_DIVIDER = ": "
    }
}

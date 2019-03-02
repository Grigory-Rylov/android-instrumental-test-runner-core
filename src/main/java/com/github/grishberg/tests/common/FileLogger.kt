package com.github.grishberg.tests.common

import org.apache.logging.log4j.LogManager

class FileLogger : RunnerLogger {
    private val logger = LogManager.getLogger(FileLogger::class.java.name)

    override fun w(tag: String, message: String) {
        logger.warn("$tag : $message")
    }

    override fun i(tag: String, message: String) {
        logger.info("$tag : $message")
    }

    override fun i(tag: String, msgFormat: String, vararg args: Any?) {
        logger.info("$tag : $msgFormat", args)
    }

    override fun d(tag: String, message: String?) {
        logger.debug("$tag : $message")
    }

    override fun d(tag: String, msgFormat: String, vararg args: Any?) {
        logger.debug("$tag : $msgFormat", args)
    }

    override fun e(tag: String, message: String?) {
        logger.error("$tag : $message")
    }

    override fun e(tag: String, message: String?, throwable: Throwable?) {
        logger.error("$tag : $message", throwable)
    }

    override fun w(tag: String, msgFormat: String, vararg args: Any?) {
        logger.warn("$tag : $msgFormat", args)
    }
}
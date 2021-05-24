package com.yandex.tests

import com.github.grishberg.tests.common.RunnerLogger
import java.io.PrintWriter
import java.io.StringWriter
import java.util.IllegalFormatException


/**
 * Simple logger that logs all logging levels to stdout.
 * Timestamp diff tag added to each log (number of seconds after test runner start).
 */
open class VerboseLogger : RunnerLogger {
    private val startTime = System.currentTimeMillis()

    private fun log(msg: String) {
        val stamp = (System.currentTimeMillis() - startTime).toFloat() / 1000.0f
        println("[$stamp s] $msg")
    }

    private fun format(format: String, vararg  args: Any): String {
        return try {
            String.format(format.replace("{}", "%s"), *args.map(Any::toString).toTypedArray())
        } catch (e: IllegalFormatException) {
            "$format [${args.joinToString(", ")}]"
        }
    }

    override fun i(tag: String, message: String) {
        log("I: [$tag] $message")
    }

    override fun i(tag: String, msgFormat: String, vararg args: Any) {
        log("I: [$tag] ${format(msgFormat, *args)}")
    }

    override fun w(tag: String, message: String) {
        log("W: [$tag] $message")
    }

    override fun w(tag: String, msgFormat: String, vararg args: Any) {
        log("W: [$tag] ${format(msgFormat, *args)}")
    }

    override fun e(tag: String, message: String) {
        log("E: [$tag] $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        val out = StringWriter()
        throwable.printStackTrace(PrintWriter(out))

        log("E: [$tag] $message: ${out.buffer}")
    }

    override fun d(tag: String, message: String) {
        log("D: [$tag] $message")
    }

    override fun d(tag: String, msgFormat: String, vararg args: Any) {
        log("D: [$tag] ${format(msgFormat, *args)}")
    }
}

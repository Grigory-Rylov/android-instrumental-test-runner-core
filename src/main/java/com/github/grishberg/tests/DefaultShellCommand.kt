package com.github.grishberg.tests

import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IShellOutputReceiver
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Function

/**
 * [ShellCommand] implementation class for wide usage.
 * Reduces the chance of secure information leak.
 *
 * Use this class with [ConnectedDeviceWrapper.executeShellCommand] method in favor of calling older
 * [ConnectedDeviceWrapper.executeShellCommand] methods.
 *
 * @param command ADB shell command to execute on device
 * @param commandSanitizer Function to return command suitable for logging.
 * @param maxTimeout The maximum timeout for the command to return.
 * @param maxTimeToOutputResponse The maximum amount of time during which the command is
 * allowed to not output any response.
 * @param maxTimeUnits Units for non-zero maxTimeout and maxTimeToOutputResponse values.
 * @param receiver The IShellOutputReceiver that will receives the output of the shell command.
 */
class DefaultShellCommand @JvmOverloads constructor(
        private val command: String,
        private val commandSanitizer: Function<String, String> = CHECKING_SANITIZER,
        private val maxTimeout: Long = DEFAULT_MAX_TIME,
        private val maxTimeToOutputResponse: Long = DEFAULT_MAX_TIME_TO_OUTPUT,
        private val maxTimeUnits: TimeUnit = DEFAULT_TIME_UNITS,
        private val receiver: IShellOutputReceiver = CollectingOutputReceiver()) : ShellCommand {

    private val loggedCommand: String

    override fun getCommand(): String {
        return command
    }

    override fun getLoggedCommand(): String {
        return loggedCommand
    }

    override fun getMaxTimeout(): Long {
        return maxTimeout
    }

    override fun getMaxTimeToOutputResponse(): Long {
        return maxTimeToOutputResponse
    }

    override fun getMaxTimeUnits(): TimeUnit {
        return maxTimeUnits
    }

    override fun getReceiver(): IShellOutputReceiver {
        return receiver
    }

    companion object {
        private val DEFAULT_TIME_UNITS = TimeUnit.MINUTES
        private const val DEFAULT_MAX_TIME_TO_OUTPUT = 5L
        private const val DEFAULT_MAX_TIME = 0L

        val SECURE_WORDS = listOf("token", "password", "secret", "AQAD-")

        /**
         * Simple sanitizer for logging.
         * Use it if you are sure that command has no secure information.
         */
        val UNSECURE_SANITIZER = Function { command: String -> command }

        /**
         * Throws exception if command looks like containing secure information.
         */
        val CHECKING_SANITIZER = Function { command: String ->
            SECURE_WORDS.forEach(Consumer { word: String ->
                if (StringUtils.containsIgnoreCase(command, word)) {
                    throw RuntimeException(String.format("Your command contains \"%s\" word. " +
                            "It is probably unsafe.", word))
                }
            })
            command
        }
    }

    init {
        loggedCommand = commandSanitizer.apply(command)
    }
}

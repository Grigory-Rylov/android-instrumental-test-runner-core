package com.github.grishberg.tests

import com.android.ddmlib.IDevice
import com.github.grishberg.tests.common.RunnerLogger
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.yandex.tests.VerboseLogger
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class DefaultShellCommandTest {
    var device: IDevice = mock() {
        on { name } doReturn ("test_device")
    }
    private val logger: RunnerLogger = spy(VerboseLogger())
    private val deviceWrapper: ConnectedDeviceWrapper = ConnectedDeviceWrapper(device, logger)

    @Test(expected = RuntimeException::class)
    fun throwIfSomethingSecureLogged() {
        deviceWrapper.executeShellCommand(
                DefaultShellCommand("echo YAV_TOKEN=AQAD-qJ1234567890 > /sdcard/.token"))

        verifyNoMoreInteractions(logger)
    }

    @Test
    fun okTest() {
        val command: ShellCommand = DefaultShellCommand("echo Hello world! > /sdcard/init.txt")

        deviceWrapper.executeShellCommand(command)

        Assert.assertEquals("echo Hello world! > /sdcard/init.txt", command.loggedCommand)
    }

    @Test
    fun customizedSanitizer() {
        val command: ShellCommand = DefaultShellCommand(
                "echo YAV_TOKEN=AQAD-qJ1234567890 > /sdcard/.token",
                commandSanitizer={ s: String ->
                    s.replace("AQAD-qJ1234567890", "HIDDEN")
                })

        deviceWrapper.executeShellCommand(command)

        Assert.assertEquals("echo YAV_TOKEN=HIDDEN > /sdcard/.token", command.loggedCommand)
        verify(logger).d(eq("test_device / ConnectedDeviceWrapper"),
                eq("Execute shell command \"{}\""),
                eq("echo YAV_TOKEN=HIDDEN > /sdcard/.token"))
    }

    @Test
    fun customTimeouts() {
        val command: ShellCommand = DefaultShellCommand("command",
                DefaultShellCommand.CHECKING_SANITIZER, 5L, 1L, TimeUnit.SECONDS)

        deviceWrapper.executeShellCommand(command)

        verify(device).executeShellCommand(eq("command"), any(),
                eq(5L), eq(1L), eq(TimeUnit.SECONDS))
    }
}

package com.github.grishberg.tests

import com.github.grishberg.tests.commands.CommandExecutionException

/**
 * Executes commands for online devices.
 */
interface DeviceCommandsRunner {
    @Throws(InterruptedException::class, CommandExecutionException::class)
    fun runCommands(devices: List<ConnectedDeviceWrapper>, context: TestRunnerContext): Boolean
}

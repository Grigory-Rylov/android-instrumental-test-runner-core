package com.github.grishberg.tests

import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider

/**
 * Creates instance of [DeviceCommandsRunner]
 */
interface CommandsRunnerFactory {
    fun provideDeviceCommandRunner(commandProvider: DeviceRunnerCommandProvider): DeviceCommandsRunner
}

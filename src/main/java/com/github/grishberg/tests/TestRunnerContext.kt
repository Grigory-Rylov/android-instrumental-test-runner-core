package com.github.grishberg.tests

import com.github.grishberg.tests.commands.TestRunnerBuilder
import com.github.grishberg.tests.common.RunnerLogger

/**
 * Provides data for test command execution.
 */
interface TestRunnerContext {
    fun getInstrumentalInfo(): InstrumentalExtension
    fun getEnvironment(): Environment
    fun getScreenshotRelation(): Map<String, String>
    fun getLogger(): RunnerLogger
    fun createTestRunnerBuilder(projectName: String,
                                testName: String,
                                instrumentationArgs: Map<String, String>,
                                targetDevice: ConnectedDeviceWrapper,
                                xmlDelegate: XmlReportGeneratorDelegate): TestRunnerBuilder

    fun setProcessCrashHandler(handler: ProcessCrashHandler)
    fun getProcessCrashedHandler(): ProcessCrashHandler
}
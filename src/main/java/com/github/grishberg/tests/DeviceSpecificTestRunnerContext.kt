package com.github.grishberg.tests

import com.github.grishberg.tests.commands.TestRunnerBuilder
import com.github.grishberg.tests.common.DeviceRunnerLogger

/**
 * Wrapper for [TestRunnerContext] that holds reference to current [ConnectedDeviceWrapper]
 */
internal class DeviceSpecificTestRunnerContext(
        device: ConnectedDeviceWrapper,
        private val context: InternalContext
) : TestRunnerContext {
    private val logger = DeviceRunnerLogger(device, context.getLogger())

    override fun getInstrumentalInfo() = context.getInstrumentalInfo()

    override fun getEnvironment() = context.getEnvironment()

    override fun getScreenshotRelation() = context.getScreenshotRelation()

    /**
     * Returns [DeviceRunnerLogger] with current device reference.
     * Automatically appends device name to TAG.
     */
    override fun getLogger() = logger

    override fun createTestRunnerBuilder(
            projectName: String,
            testName: String,
            instrumentationArgs: Map<String, String>,
            targetDevice: ConnectedDeviceWrapper,
            xmlDelegate: XmlReportGeneratorDelegate): TestRunnerBuilder {
        return return TestRunnerBuilder(projectName,
                testName,
                instrumentationArgs,
                targetDevice,
                this,
                xmlDelegate)
    }

    override fun setProcessCrashHandler(handler: ProcessCrashHandler) {
        context.setProcessCrashHandler(handler)
    }

    override fun getProcessCrashedHandler() = context.getProcessCrashedHandler()
}
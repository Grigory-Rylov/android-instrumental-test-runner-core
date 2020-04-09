package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.TestRunnerBuilder;
import com.github.grishberg.tests.common.DeviceRunnerLogger;
import com.github.grishberg.tests.common.RunnerLogger;

import java.util.Map;

/**
 * Provides data for test command execution and holds reference to current device.
 */
public class TestRunnerContextImpl implements TestRunnerContext {
    private final InstrumentalExtension instrumentalInfo;
    private final Environment environment;
    private final Map<String, String> screenshotRelation;
    private final RunnerLogger logger;
    private ProcessCrashHandler processCrashHandler = ProcessCrashHandler.STUB.INSTANCE;

    public TestRunnerContextImpl(InstrumentalExtension instrumentalInfo,
                                 Environment environment,
                                 Map<String, String> screenshotRelation,
                                 RunnerLogger logger) {
        this.instrumentalInfo = instrumentalInfo;
        this.environment = environment;
        this.screenshotRelation = screenshotRelation;
        this.logger = logger;
    }

    @Override
    public InstrumentalExtension getInstrumentalInfo() {
        return new InstrumentalExtension(instrumentalInfo);
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public Map<String, String> getScreenshotRelation() {
        return screenshotRelation;
    }

    @Override
    public RunnerLogger getLogger() {
        return logger;
    }

    @Override
    public TestRunnerBuilder createTestRunnerBuilder(String projectName,
                                                     String testName,
                                                     Map<String, String> instrumentationArgs,
                                                     ConnectedDeviceWrapper targetDevice,
                                                     XmlReportGeneratorDelegate xmlDelegate) {
        return new TestRunnerBuilder(projectName,
                testName,
                instrumentationArgs,
                targetDevice,
                this,
                xmlDelegate);
    }

    @Override
    public void setProcessCrashHandler(ProcessCrashHandler handler) {
        processCrashHandler = handler;
    }

    @Override
    public ProcessCrashHandler getProcessCrashedHandler() {
        return processCrashHandler;
    }
}

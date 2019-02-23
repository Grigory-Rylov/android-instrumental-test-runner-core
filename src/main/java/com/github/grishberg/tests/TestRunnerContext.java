package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.TestRunnerBuilder;
import com.github.grishberg.tests.common.RunnerLogger;

import java.util.Map;

/**
 * Provides data for test command execution.
 */
public class TestRunnerContext {
    private final InstrumentalExtension instrumentalInfo;
    private final Environment environment;
    private final Map<String, String> screenshotRelation;
    private final RunnerLogger logger;
    private ProcessCrashHandler processCrashHandler = ProcessCrashHandler.STUB.INSTANCE;

    public TestRunnerContext(InstrumentalExtension instrumentalInfo,
                             Environment environment,
                             Map<String, String> screenshotRelation,
                             RunnerLogger logger) {
        this.instrumentalInfo = instrumentalInfo;
        this.environment = environment;
        this.screenshotRelation = screenshotRelation;
        this.logger = logger;
    }

    public InstrumentalExtension getInstrumentalInfo() {
        return instrumentalInfo;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Map<String, String> getScreenshotRelation() {
        return screenshotRelation;
    }

    public RunnerLogger getLogger() {
        return logger;
    }

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

    void setProcessCrashHandler(ProcessCrashHandler handler) {
        processCrashHandler = handler;
    }

    public ProcessCrashHandler getProcessCrashedHandler() {
        return processCrashHandler;
    }
}

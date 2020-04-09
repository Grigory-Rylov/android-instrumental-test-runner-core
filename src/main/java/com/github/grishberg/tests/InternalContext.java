package com.github.grishberg.tests;

import com.github.grishberg.tests.common.RunnerLogger;

import java.util.Map;

/**
 * Provides data for DeviceCommandsRunner.
 */
class InternalContext {
    private final InstrumentalExtension instrumentalInfo;
    private final Environment environment;
    private final Map<String, String> screenshotRelation;
    private final RunnerLogger logger;
    private ProcessCrashHandler processCrashHandler = ProcessCrashHandler.STUB.INSTANCE;

    InternalContext(InstrumentalExtension instrumentalInfo,
                    Environment environment,
                    Map<String, String> screenshotRelation,
                    RunnerLogger logger) {
        this.instrumentalInfo = instrumentalInfo;
        this.environment = environment;
        this.screenshotRelation = screenshotRelation;
        this.logger = logger;
    }

    InstrumentalExtension getInstrumentalInfo() {
        return new InstrumentalExtension(instrumentalInfo);
    }

    Environment getEnvironment() {
        return environment;
    }

    Map<String, String> getScreenshotRelation() {
        return screenshotRelation;
    }

    RunnerLogger getLogger() {
        return logger;
    }

    void setProcessCrashHandler(ProcessCrashHandler handler) {
        processCrashHandler = handler;
    }

    ProcessCrashHandler getProcessCrashedHandler() {
        return processCrashHandler;
    }
}

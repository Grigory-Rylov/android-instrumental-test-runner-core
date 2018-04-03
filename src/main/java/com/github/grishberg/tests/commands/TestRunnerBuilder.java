package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;

import java.util.Map;

/**
 * Helper class builds RemoteAndroidTestRunner.
 */
class TestRunnerBuilder {
    private final RemoteAndroidTestRunner runner;
    private final String coverageFile;

    TestRunnerBuilder(InstrumentalPluginExtension instrumentationInfo,
                      Map<String, String> instrumentationArgs,
                      ConnectedDeviceWrapper targetDevice) {
        runner = new RemoteAndroidTestRunner(
                instrumentationInfo.getInstrumentalPackage(),
                instrumentationInfo.getInstrumentalRunner(),
                targetDevice.getDevice());
        for (Map.Entry<String, String> arg : instrumentationArgs.entrySet()) {
            runner.addInstrumentationArg(arg.getKey(), arg.getValue());
        }
        coverageFile = "/data/data/" + instrumentationInfo.getApplicationId()
                + "/" + ConnectedDeviceWrapper.COVERAGE_FILE_NAME;
        if (instrumentationInfo.isCoverageEnabled()) {
            runner.addInstrumentationArg("coverage", "true");
            runner.addInstrumentationArg("coverageFile", coverageFile);
        }
    }

    RemoteAndroidTestRunner getTestRunner() {
        return runner;
    }

    String getCoverageFile() {
        return coverageFile;
    }
}

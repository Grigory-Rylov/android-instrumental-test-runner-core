package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.utils.ILogger;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.commands.reports.EmptyScreenShotMaker;
import com.github.grishberg.tests.commands.reports.ScreenShotMaker;
import com.github.grishberg.tests.commands.reports.ScreenShotMakerImpl;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;

import java.util.Map;

/**
 * Helper class builds RemoteAndroidTestRunner.
 */
class TestRunnerBuilder {
    private final RemoteAndroidTestRunner runner;
    private final String coverageFile;
    private final TestXmlReportsGenerator testRunListener;
    private final RunTestLogger runTestLogger;

    TestRunnerBuilder(Project project,
                      InstrumentalPluginExtension instrumentationInfo,
                      Map<String, String> instrumentationArgs,
                      ConnectedDeviceWrapper targetDevice,
                      Environment environment,
                      RunnerLogger logger) {
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

        ScreenShotMaker screenShotMaker = new EmptyScreenShotMaker();
        if (instrumentationInfo.isMakeScreenshotsWhenFail()) {
            screenShotMaker = new ScreenShotMakerImpl(environment.getReportsDir(), targetDevice, logger);
        }

        runTestLogger = new RunTestLogger(logger);

        testRunListener = new TestXmlReportsGenerator(targetDevice.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                "",
                runTestLogger,
                screenShotMaker
        );
        testRunListener.setReportDir(environment.getReportsDir());
    }

    RemoteAndroidTestRunner getTestRunner() {
        return runner;
    }

    String getCoverageFile() {
        return coverageFile;
    }

    TestXmlReportsGenerator getTestRunListener() {
        return testRunListener;
    }

    public ILogger getRunTestLogger() {
        return runTestLogger;
    }
}

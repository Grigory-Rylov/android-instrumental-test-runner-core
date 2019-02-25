package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.utils.ILogger;
import com.github.grishberg.tests.*;
import com.github.grishberg.tests.commands.reports.*;
import com.github.grishberg.tests.common.RunnerLogger;

import java.util.Map;

/**
 * Helper class builds RemoteAndroidTestRunner.
 */
public class TestRunnerBuilder {
    private final RemoteAndroidTestRunner runner;
    private final String coverageFile;
    private final TestXmlReportsGenerator testRunListener;
    private final RunTestLogger runTestLogger;

    public TestRunnerBuilder(String projectName,
                             String testGroupPrefix,
                             Map<String, String> instrumentationArgs,
                             ConnectedDeviceWrapper targetDevice,
                             TestRunnerContext context,
                             XmlReportGeneratorDelegate xmlDelegate) {
        InstrumentalExtension instrumentationInfo = context.getInstrumentalInfo();
        Environment environment = context.getEnvironment();
        RunnerLogger logger = context.getLogger();

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

        ScreenShotMaker screenShotMaker = getScreenShotMaker(context.getScreenshotRelation(),
                instrumentationInfo, targetDevice, environment, logger);
        LogcatSaver logcatSaver = getLogcatSaver(instrumentationInfo, targetDevice, environment, logger);

        runTestLogger = new RunTestLogger(logger);

        testRunListener = new TestXmlReportsGenerator(targetDevice.getName(),
                projectName,
                instrumentationInfo.getFlavorName(),
                testGroupPrefix,
                runTestLogger,
                screenShotMaker,
                logcatSaver,
                xmlDelegate);
        testRunListener.setReportDir(environment.getResultsDir());
    }

    private ScreenShotMaker getScreenShotMaker(Map<String, String> screenshotMap,
                                               InstrumentalExtension instrumentationInfo,
                                               ConnectedDeviceWrapper targetDevice,
                                               Environment environment,
                                               RunnerLogger logger) {
        if (instrumentationInfo.isMakeScreenshotsWhenFail()) {
            return new ScreenShotMakerImpl(screenshotMap, environment.getReportsDir(), targetDevice,
                    logger);
        }
        return new EmptyScreenShotMaker();
    }

    private LogcatSaver getLogcatSaver(InstrumentalExtension instrumentationInfo,
                                       ConnectedDeviceWrapper targetDevice,
                                       Environment environment,
                                       RunnerLogger logger) {
        if (instrumentationInfo.isSaveLogcat()) {
            return new LogcatSaverImpl(targetDevice, environment.getReportsDir(), logger);
        }
        return new EmptyLogcatSaver();
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

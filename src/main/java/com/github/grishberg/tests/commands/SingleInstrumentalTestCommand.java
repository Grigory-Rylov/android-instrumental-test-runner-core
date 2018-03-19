package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.parser.TestPlanElement;
import org.gradle.api.Project;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes instrumentation test for single test method.
 */
public class SingleInstrumentalTestCommand implements DeviceRunnerCommand {
    private static final String TAG = SingleInstrumentalTestCommand.class.getSimpleName();
    private static final String CLASS = "class";
    private static final String PACKAGE = "package";
    private final Project project;
    private String testName;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;
    private File coverageOutputDir;
    private File resultsDir;
    private RunnerLogger logger;

    public SingleInstrumentalTestCommand(Project project,
                                         String testReportSuffix,
                                         InstrumentalPluginExtension instrumentalInfo,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution,
                                         File coverageFilesDir,
                                         File resultsDir,
                                         RunnerLogger logger) {
        this.project = project;
        this.testName = testReportSuffix;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = new HashMap<>(instrumentalArgs);
        this.coverageOutputDir = coverageFilesDir;
        this.resultsDir = resultsDir;
        this.logger = logger;

        initTargetTestArgs(testForExecution);
    }

    private void initTargetTestArgs(List<TestPlanElement> testForExecution) {
        StringBuilder sbClass = new StringBuilder();
        StringBuilder sbPackage = new StringBuilder();
        for (int i = 0; i < testForExecution.size(); i++) {
            TestPlanElement plan = testForExecution.get(i);
            if (plan.isPackage()) {
                if (sbPackage.length() > 0) {
                    sbPackage.append(",");
                }
                sbPackage.append(plan.getAmInstrumentCommand());
                continue;
            }

            if (sbClass.length() > 0) {
                sbClass.append(",");
            }
            sbClass.append(plan.getAmInstrumentCommand());
        }

        if (sbClass.length() > 0) {
            instrumentationArgs.put(CLASS, sbClass.toString());
        }
        if (sbPackage.length() > 0) {
            instrumentationArgs.put(PACKAGE, sbPackage.toString());
        }
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice) throws ExecuteCommandException {
        DeviceCommandResult result = new DeviceCommandResult();

        RemoteAndroidTestRunner runner = new RemoteAndroidTestRunner(
                instrumentationInfo.getInstrumentalPackage(),
                instrumentationInfo.getInstrumentalRunner(),
                targetDevice.getDevice());

        for (Map.Entry<String, String> arg : instrumentationArgs.entrySet()) {
            runner.addInstrumentationArg(arg.getKey(), arg.getValue());
        }

        String coverageFile = "/data/data/" + instrumentationInfo.getApplicationId()
                + "/" + ConnectedDeviceWrapper.COVERAGE_FILE_NAME;
        if (instrumentationInfo.isCoverageEnabled()) {
            runner.addInstrumentationArg("coverage", "true");
            runner.addInstrumentationArg("coverageFile", coverageFile);
        }

        RunTestLogger runTestLogger = new RunTestLogger(logger);
        String singleTestMethodPrefix = targetDevice.getName() + "#" + testName;
        TestXmlReportsGenerator testRunListener = new TestXmlReportsGenerator(targetDevice.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                singleTestMethodPrefix,
                runTestLogger
        );
        testRunListener.setReportDir(resultsDir);

        try {
            runner.run(testRunListener);
            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());

            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        singleTestMethodPrefix,
                        coverageFile,
                        coverageOutputDir,
                        runTestLogger);
            }
        } catch (Exception e) {
            logger.e(TAG, "InstrumentalTestCommand.execute: Exception", e);
        }
        return result;
    }

    @Override
    public String toString() {
        return "SingleInstrumentalTestCommand{ " + instrumentationArgs + " }";
    }
}

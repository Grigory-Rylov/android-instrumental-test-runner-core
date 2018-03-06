package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.planner.parser.TestPlan;
import org.gradle.api.Project;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes instrumentation test for single test method.
 */
public class SingleInstrumentalTestCommand implements DeviceRunnerCommand {
    private static final String CLASS = "class";
    private final Project project;
    private String testName;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;
    private File coverageOuptutDir;
    private File resultsDir;

    public SingleInstrumentalTestCommand(Project project,
                                         String testReportSuffix,
                                         InstrumentalPluginExtension instrumentalInfo,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlan> testForExecution,
                                         File coverageFilesDir,
                                         File resultsDir) {
        this.project = project;
        this.testName = testReportSuffix;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = new HashMap<>(instrumentalArgs);
        this.coverageOuptutDir = coverageFilesDir;
        this.resultsDir = resultsDir;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testForExecution.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            TestPlan plan = testForExecution.get(i);
            sb.append(plan.getClassName());
            sb.append("#");
            sb.append(plan.getMethodName());
        }
        instrumentationArgs.put(CLASS, sb.toString());
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

        RunTestLogger runTestLogger = new RunTestLogger(project.getLogger());
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
                        coverageOuptutDir,
                        runTestLogger);
            }
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestCommand.execute: Exception", e);
        }
        return result;
    }

    @Override
    public String toString() {
        return "SingleInstrumentalTestCommand{}";
    }
}

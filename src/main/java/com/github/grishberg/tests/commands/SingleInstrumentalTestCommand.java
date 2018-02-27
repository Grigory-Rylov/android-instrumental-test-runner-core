package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.DeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.planner.parser.TestPlan;
import org.gradle.api.Project;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes instrumentation test for single test method.
 */
public class SingleInstrumentalTestCommand implements DeviceCommand {
    private static final String CLASS = "class";
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;
    private File coverageOuptutDir;
    private File resultsDir;

    public SingleInstrumentalTestCommand(Project project,
                                         InstrumentalPluginExtension instrumentalInfo,
                                         Map<String, String> instrumentalArgs,
                                         TestPlan currentPlan,
                                         File coverageFilesDir,
                                         File resultsDir) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = new HashMap<>(instrumentalArgs);
        this.coverageOuptutDir = coverageFilesDir;
        this.resultsDir = resultsDir;
        instrumentationArgs.put(CLASS,
                String.format("%s#%s", currentPlan.getClassName(), currentPlan.getName()));
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper targetDevice) throws ExecuteCommandException {
        DeviceCommandResult result = new DeviceCommandResult();

        RemoteAndroidTestRunner runner = new RemoteAndroidTestRunner(
                instrumentationInfo.getInstrumentalPackage(),
                instrumentationInfo.getInstrumentalRunner(),
                targetDevice.getDevice());

        for (Map.Entry<String, String> arg : instrumentationArgs.entrySet()) {
            runner.addInstrumentationArg(arg.getKey(), arg.getValue());
        }

        String coverageFile = "/data/data/" + instrumentationInfo.getApplicationId()
                + "/" + DeviceWrapper.COVERAGE_FILE_NAME;
        if (instrumentationInfo.isCoverageEnabled()) {
            runner.addInstrumentationArg("coverage", "true");
            runner.addInstrumentationArg("coverageFile", coverageFile);
        }

        RunTestLogger runTestLogger = new RunTestLogger(project.getLogger());
        String singleTestMethodPrefix = targetDevice.getName() + "#" + instrumentationArgs.get(CLASS);
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

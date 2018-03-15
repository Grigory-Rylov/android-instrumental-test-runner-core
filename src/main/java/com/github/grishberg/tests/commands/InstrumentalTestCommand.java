package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;

import java.io.File;
import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceRunnerCommand {
    private final Project project;
    private RunnerLogger logger;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;
    private File coverageOutputDir;
    private File reportsDir;

    public InstrumentalTestCommand(Project project,
                                   InstrumentalPluginExtension instrumentalInfo,
                                   Map<String, String> instrumentalArgs,
                                   File coverageFilesDir,
                                   File reportsDir,
                                   RunnerLogger logger) {
        this.project = project;
        this.logger = logger;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = instrumentalArgs;
        this.coverageOutputDir = coverageFilesDir;
        this.reportsDir = reportsDir;
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
        TestXmlReportsGenerator testRunListener = new TestXmlReportsGenerator(targetDevice.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                "",
                runTestLogger
        );
        testRunListener.setReportDir(reportsDir);

        try {
            runner.run(testRunListener);
            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());
            String coverageOutFilePrefix = targetDevice.getName();
            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        coverageOutFilePrefix,
                        coverageFile,
                        coverageOutputDir,
                        runTestLogger);
            }
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestCommand.execute: Exception", e);
        }
        return result;
    }

    private String provideDeviceNameForReport(ConnectedDeviceWrapper targetDevice) {
        String prefix = "";
        if (targetDevice.isEmulator()) {
            prefix = "(AVD)";
        }
        String targetDeviceName = targetDevice.getName();
        return targetDeviceName != null ?
                targetDeviceName + prefix : targetDevice.getSerialNumber();
    }
}

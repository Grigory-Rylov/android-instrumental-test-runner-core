package com.github.grishberg.tests.commands;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.android.utils.ILogger;
import com.github.grishberg.tests.DeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceCommand {
    private static final String COVERAGE_FILE_NAME = "coverage.ec";
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;
    private final Logger logger;
    private File coverageDir;
    private File reportsDir;

    public InstrumentalTestCommand(Project project,
                                   InstrumentalPluginExtension instrumentalInfo,
                                   Map<String, String> instrumentalArgs,
                                   File coverageFilesDir,
                                   File reportsDir) {
        this.project = project;
        this.logger = project.getLogger();
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = instrumentalArgs;
        this.coverageDir = coverageFilesDir;
        this.reportsDir = reportsDir;
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
                + "/" + COVERAGE_FILE_NAME;
        if (instrumentationInfo.isCoverageEnabled()) {
            runner.addInstrumentationArg("coverage", "true");
            runner.addInstrumentationArg("coverageFile", coverageFile);
        }

        RunTestLogger runTestLogger = new RunTestLogger(project.getLogger());
        CustomTestRunListener testRunListener = new CustomTestRunListener(targetDevice.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                runTestLogger
        );
        testRunListener.setReportDir(reportsDir);

        try {
            runner.run(testRunListener);
            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());

            pullCoverageFile(targetDevice, coverageFile, runTestLogger);
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestCommand.execute: Exception", e);
        }
        return result;
    }

    private void pullCoverageFile(DeviceWrapper targetDevice,
                                  String coverageFile,
                                  final ILogger logger) {
        MultiLineReceiver outputReceiver = new MultiLineReceiver() {
            public void processNewLines(String[] lines) {
                for (String line : lines) {
                    logger.verbose(line);
                }
            }

            public boolean isCancelled() {
                return false;
            }
        };

        String targetDeviceName = targetDevice.getName();
        logger.verbose("TestRunnerCommand '%s': fetching coverage data from %s",
                targetDeviceName, coverageFile);
        try {
            String temporaryCoverageCopy = "/data/local/tmp/" + instrumentationInfo.getApplicationId()
                    + "." + COVERAGE_FILE_NAME;
            targetDevice.executeShellCommand("run-as " + instrumentationInfo.getApplicationId() +
                            " cat " + coverageFile + " | cat > " + temporaryCoverageCopy,
                    outputReceiver,
                    30L, TimeUnit.SECONDS);
            targetDevice.pullFile(temporaryCoverageCopy,
                    (new File(coverageDir, targetDeviceName + "-" + COVERAGE_FILE_NAME))
                            .getPath());
            targetDevice.executeShellCommand("rm " + temporaryCoverageCopy, outputReceiver, 30L,
                    TimeUnit.SECONDS);
        } catch (Exception e) {
            project.getLogger().error("Exception while pulling coverage file", e);
        }
    }

    private String provideDeviceNameForReport(DeviceWrapper targetDevice) {
        String prefix = "";
        if (targetDevice.isEmulator()) {
            prefix = "(AVD)";
        }
        String targetDeviceName = targetDevice.getName();
        return targetDeviceName != null ?
                targetDeviceName + prefix : targetDevice.getSerialNumber();
    }
}

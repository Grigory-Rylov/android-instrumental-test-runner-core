package com.github.grishberg.tests.commands;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.DeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import org.gradle.api.Project;

import java.io.File;
import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceCommand {
    private static final String COVERAGE_FILE_NAME = "coverage.ec";
    private final Project project;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;
    private File coverageFilesDir;
    private File reportsDir;

    public InstrumentalTestCommand(Project project,
                                   InstrumentalPluginExtension instrumentalInfo,
                                   Map<String, String> instrumentalArgs,
                                   File coverageFilesDir,
                                   File reportsDir) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = instrumentalArgs;
        this.coverageFilesDir = coverageFilesDir;
        this.reportsDir = reportsDir;
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper device) {
        DeviceCommandResult result = new DeviceCommandResult();

        RemoteAndroidTestRunner runner = new RemoteAndroidTestRunner(
                instrumentationInfo.getInstrumentalPackage(),
                instrumentationInfo.getInstrumentalRunner(),
                device.getDevice());

        for (Map.Entry<String, String> arg : instrumentationArgs.entrySet()) {
            runner.addInstrumentationArg(arg.getKey(), arg.getValue());
        }

        CustomTestRunListener testRunListener = new CustomTestRunListener(device.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                new RunTestLogger(project.getLogger())
        );
        testRunListener.setReportDir(reportsDir);

        try {
            runner.run(testRunListener);
            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestCommand.execute: Exception", e);
        }
        return result;
    }
}

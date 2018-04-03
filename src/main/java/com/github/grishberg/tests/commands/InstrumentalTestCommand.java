package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.RunTestLogger;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;

import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceRunnerCommand {
    private final Project project;
    private Environment environment;
    private RunnerLogger logger;
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Map<String, String> instrumentationArgs;

    public InstrumentalTestCommand(Project project,
                                   InstrumentalPluginExtension instrumentalInfo,
                                   Map<String, String> instrumentalArgs,
                                   Environment environment,
                                   RunnerLogger logger) {
        this.project = project;
        this.environment = environment;
        this.logger = logger;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = instrumentalArgs;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice) throws ExecuteCommandException {
        DeviceCommandResult result = new DeviceCommandResult();

        TestRunnerBuilder testRunnerBuilder = new TestRunnerBuilder(instrumentationInfo,
                instrumentationArgs,
                targetDevice);

        RunTestLogger runTestLogger = new RunTestLogger(logger);
        TestXmlReportsGenerator testRunListener = new TestXmlReportsGenerator(targetDevice.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                "",
                runTestLogger
        );
        testRunListener.setReportDir(environment.getReportsDir());

        try {
            testRunnerBuilder.getTestRunner().run(testRunListener);
            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());
            String coverageOutFilePrefix = targetDevice.getName();
            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        coverageOutFilePrefix,
                        testRunnerBuilder.getCoverageFile(),
                        environment.getCoverageDir(),
                        runTestLogger);
            }
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestCommand.execute: Exception", e);
        }
        return result;
    }
}

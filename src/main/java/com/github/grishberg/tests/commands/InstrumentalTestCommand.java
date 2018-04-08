package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import org.gradle.api.Project;

import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceRunnerCommand {
    private final Project project;
    private final Map<String, String> instrumentationArgs;

    public InstrumentalTestCommand(Project project,
                                   Map<String, String> instrumentalArgs) {
        this.project = project;
        this.instrumentationArgs = instrumentalArgs;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice, TestRunnerContext context)
            throws ExecuteCommandException {
        DeviceCommandResult result = new DeviceCommandResult();
        Environment environment = context.getEnvironment();
        InstrumentalPluginExtension instrumentationInfo = context.getInstrumentalInfo();

        TestRunnerBuilder testRunnerBuilder = new TestRunnerBuilder(project,
                instrumentationArgs,
                targetDevice,
                context);

        try {
            TestXmlReportsGenerator testRunListener = testRunnerBuilder.getTestRunListener();

            testRunnerBuilder.getTestRunner().run(testRunListener);

            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());
            String coverageOutFilePrefix = targetDevice.getName();

            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        coverageOutFilePrefix,
                        testRunnerBuilder.getCoverageFile(),
                        environment.getCoverageDir(),
                        testRunnerBuilder.getRunTestLogger());
            }
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestCommand.execute: Exception", e);
            throw new ExecuteCommandException("InstrumentalTestCommand.execute failed:", e);
        }
        return result;
    }
}

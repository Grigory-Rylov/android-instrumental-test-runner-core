package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;

import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceRunnerCommand {
    private final String projectName;
    private final Map<String, String> instrumentationArgs;

    public InstrumentalTestCommand(String projectName,
                                   Map<String, String> instrumentalArgs) {
        this.projectName = projectName;
        this.instrumentationArgs = instrumentalArgs;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice, TestRunnerContext context)
            throws ExecuteCommandException {
        DeviceCommandResult result = new DeviceCommandResult();
        Environment environment = context.getEnvironment();
        InstrumentalExtension instrumentationInfo = context.getInstrumentalInfo();

        TestRunnerBuilder testRunnerBuilder = new TestRunnerBuilder(projectName,
                "",
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
            throw new ExecuteCommandException("InstrumentalTestCommand.execute failed:", e);
        }
        return result;
    }
}

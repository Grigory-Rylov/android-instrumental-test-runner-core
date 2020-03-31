package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.*;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;

import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceRunnerCommand {
    private final String projectName;
    private final Map<String, String> instrumentationArgs;
    private XmlReportGeneratorDelegate xmlReportGeneratorDelegate;

    public InstrumentalTestCommand(String projectName,
                                   Map<String, String> instrumentalArgs) {
        this(projectName, instrumentalArgs, XmlReportGeneratorDelegate.STUB.INSTANCE);
    }

    public InstrumentalTestCommand(String projectName,
                                   Map<String, String> instrumentalArgs,
                                   XmlReportGeneratorDelegate xmlReportGeneratorDelegate) {
        this.projectName = projectName;
        this.instrumentationArgs = instrumentalArgs;
        this.xmlReportGeneratorDelegate = xmlReportGeneratorDelegate;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice, TestRunnerContext context)
            throws CommandExecutionException {
        DeviceCommandResult result = new DeviceCommandResult();
        Environment environment = context.getEnvironment();
        InstrumentalExtension instrumentationInfo = context.getInstrumentalInfo();

        TestRunnerBuilder testRunnerBuilder = new TestRunnerBuilder(projectName,
                "",
                instrumentationArgs,
                targetDevice,
                context,
                xmlReportGeneratorDelegate);

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
        } catch (Throwable e) {
            throw new CommandExecutionException("InstrumentalTestCommand.execute failed:", e);
        }
        return result;
    }
}

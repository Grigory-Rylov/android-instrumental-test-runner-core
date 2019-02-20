package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.ddmlib.testrunner.TestRunResult;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.TestPlanElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes instrumentation test for single test method.
 */
public class SingleInstrumentalTestCommand implements DeviceRunnerCommand {
    private static final String CLASS = "class";
    private static final String PACKAGE = "package";
    private final String projectName;
    private String testName;
    private final Map<String, String> instrumentationArgs;

    public SingleInstrumentalTestCommand(String projectName,
                                         String testReportSuffix,
                                         Map<String, String> instrumentalArgs,
                                         List<TestPlanElement> testForExecution) {
        this.projectName = projectName;
        this.testName = testReportSuffix;
        this.instrumentationArgs = new HashMap<>(instrumentalArgs);

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
    public DeviceCommandResult execute(ConnectedDeviceWrapper targetDevice, TestRunnerContext context)
            throws CommandExecutionException {
        DeviceCommandResult result = new DeviceCommandResult();
        InstrumentalExtension instrumentationInfo = context.getInstrumentalInfo();
        Environment environment = context.getEnvironment();

        TestRunnerBuilder testRunnerBuilder = context.createTestRunnerBuilder(projectName,
                testName,
                instrumentationArgs,
                targetDevice);

        String singleTestMethodPrefix = String.format("%s#%s", targetDevice.getName(), testName);
        TestXmlReportsGenerator testRunListener = testRunnerBuilder.getTestRunListener();

        try {
            testRunnerBuilder.getTestRunner().run(testRunListener);

            TestRunResult runResult = testRunListener.getRunResult();
            result.setFailed(runResult.hasFailedTests());

            if (instrumentationInfo.isCoverageEnabled()) {
                targetDevice.pullCoverageFile(instrumentationInfo,
                        singleTestMethodPrefix,
                        testRunnerBuilder.getCoverageFile(),
                        environment.getCoverageDir(),
                        testRunnerBuilder.getRunTestLogger());
            }
        } catch (ProcessCrashedException e) {
            TestIdentifier currentTest = testRunListener.getCurrentTest();
            String failMessage = context.getProcessCrashedHandler()
                    .provideFailMessageOnProcessCrashed(targetDevice, currentTest);
            testRunListener.failLastTest(failMessage);
            testRunListener.testRunEnded(0, new HashMap<>());

            throw new CommandExecutionException("SingleInstrumentalTestCommand.execute failed:", e);
        } catch (Exception e) {
            throw new CommandExecutionException("SingleInstrumentalTestCommand.execute failed:", e);
        }
        return result;
    }

    @Override
    public String toString() {
        return "SingleInstrumentalTestCommand{ " + instrumentationArgs + " }";
    }
}

package com.grishberg.tests.commands;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.InstrumentationInfo;
import com.grishberg.tests.RunTestLogger;
import com.grishberg.tests.planner.parser.TestPlan;
import org.gradle.api.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * Executes instrumentation test for single test method.
 */
public class SingleInstrumentalTestCommand implements DeviceCommand {
    private final Project project;
    private final InstrumentationInfo instrumentationInfo;
    private final Map<String, String> instrumentationArgs;

    public SingleInstrumentalTestCommand(Project project,
                                         InstrumentationInfo instrumentalInfo,
                                         Map<String, String> instrumentalArgs,
                                         TestPlan currentPlan) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = new HashMap<>(instrumentalArgs);
        instrumentationArgs.put("class",
                String.format("%s#%s", currentPlan.getClassName(), currentPlan.getName()));
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper device) {
        DeviceCommandResult result = new DeviceCommandResult();

        RemoteAndroidTestRunner runner = new RemoteAndroidTestRunner(
                instrumentationInfo.getInstrumentalPackage(),
                instrumentationInfo.getInstrumentalRunner(),
                device);

        instrumentationArgs.put("log", "true");
        for (Map.Entry<String, String> arg : instrumentationArgs.entrySet()) {
            runner.addInstrumentationArg(arg.getKey(), arg.getValue());
        }

        CustomTestRunListener testRunListener = new CustomTestRunListener(device.getName(),
                project.getName(),
                instrumentationInfo.getFlavorName(),
                new RunTestLogger(project.getLogger())
        );

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

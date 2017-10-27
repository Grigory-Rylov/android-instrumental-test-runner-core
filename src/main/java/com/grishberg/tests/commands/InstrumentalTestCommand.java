package com.grishberg.tests.commands;

import com.android.builder.internal.testing.CustomTestRunListener;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestRunResult;
import com.android.utils.ILogger;
import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.InstrumentationInfo;
import org.gradle.api.Project;

import java.util.Map;

/**
 * Executes instrumental tests on connected device.
 */
public class InstrumentalTestCommand implements DeviceCommand {
    private final Project project;
    private final InstrumentationInfo instrumentationInfo;
    private final Map<String, String> instrumentationArgs;

    public InstrumentalTestCommand(Project project,
                                   InstrumentationInfo instrumentalInfo,
                                   Map<String, String> instrumentalArgs) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
        this.instrumentationArgs = instrumentalArgs;
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
                new RunTestLogger()
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

    private class RunTestLogger implements ILogger {
        @Override
        public void error(Throwable t, String msgFormat, Object... args) {
            project.getLogger().error(String.format(msgFormat, args), t);
        }

        @Override
        public void warning(String msgFormat, Object... args) {
            project.getLogger().warn(String.format(msgFormat, args));
        }

        @Override
        public void info(String msgFormat, Object... args) {
            project.getLogger().info(String.format(msgFormat, args));
        }

        @Override
        public void verbose(String msgFormat, Object... args) {
            project.getLogger().info(String.format(msgFormat, args));
        }
    }
}

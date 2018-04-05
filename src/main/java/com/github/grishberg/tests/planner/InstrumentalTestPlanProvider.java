package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides set of {@link TestPlanElement} for instrumental tests.
 */
public class InstrumentalTestPlanProvider {
    private static final String TAG = InstrumentalTestPlanProvider.class.getSimpleName();
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Project project;
    private final PackageTreeGenerator packageTreeGenerator;
    private RunnerLogger logger;

    public InstrumentalTestPlanProvider(Project project,
                                        InstrumentalPluginExtension instrumentationInfo,
                                        PackageTreeGenerator packageTreeGenerator,
                                        RunnerLogger logger) {
        this.project = project;
        this.instrumentationInfo = instrumentationInfo;
        this.packageTreeGenerator = packageTreeGenerator;
        this.logger = logger;
    }

    public List<TestPlanElement> provideTestPlan(ConnectedDeviceWrapper device,
                                                 Map<String, String> instrumentalArgs) {
        logger.i(TAG, "provideTestPlan for device {}", device.getName());
        HashMap<String, String> args = new HashMap<>(instrumentalArgs);
        args.put("log", "true");

        InstrumentTestLogParser receiver = new InstrumentTestLogParser();
        receiver.setLogger(new TestLogParserLogger());
        StringBuilder command = new StringBuilder("am instrument -r -w");

        args.put("listener", instrumentationInfo.getInstrumentListener());

        for (Map.Entry<String, String> arg : args.entrySet()) {
            command.append(" -e ");
            command.append(arg.getKey());
            command.append(" ");
            command.append(arg.getValue());
        }
        command.append(" ");
        command.append(instrumentationInfo.getInstrumentalPackage());
        command.append("/");
        command.append(instrumentationInfo.getInstrumentalRunner());

        try {
            device.executeShellCommand(command.toString(), receiver, 0, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.e(TAG, "InstrumentalTestPlanProvider.execute error:", e);
        }

        return receiver.getTestInstances();
    }

    /**
     * @return test holder contains all test methods in project.
     */
    public InstrumentalTestHolder provideInstrumentalTests(ConnectedDeviceWrapper device,
                                                           Map<String, String> instrumentalArgs) {
        // TODO: create fabric
        return new InstrumentalTestHolder(provideTestPlan(device, instrumentalArgs), packageTreeGenerator);
    }

    private class TestLogParserLogger implements InstrumentTestLogParser.ParserLogger {
        @Override
        public void logLine(String line) {
            project.getLogger().info(line);
        }
    }
}

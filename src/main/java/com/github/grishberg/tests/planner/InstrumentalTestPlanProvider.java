package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.planner.parser.InstrumentTestLogParser;
import com.github.grishberg.tests.planner.parser.TestPlanElement;
import org.gradle.api.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides set of {@link TestPlanElement} for instrumental tests.
 */
public class InstrumentalTestPlanProvider {
    private final InstrumentalPluginExtension instrumentationInfo;
    private final Project project;
    private final PackageTreeGenerator packageTreeGenerator;

    public InstrumentalTestPlanProvider(Project project,
                                        InstrumentalPluginExtension instrumentationInfo,
                                        PackageTreeGenerator packageTreeGenerator) {
        this.project = project;
        this.instrumentationInfo = instrumentationInfo;
        this.packageTreeGenerator = packageTreeGenerator;
    }

    public List<TestPlanElement> provideTestPlan(ConnectedDeviceWrapper device,
                                                 Map<String, String> instrumentalArgs) {
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
            project.getLogger().error("InstrumentalTestPlanProvider.execute error:", e);
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

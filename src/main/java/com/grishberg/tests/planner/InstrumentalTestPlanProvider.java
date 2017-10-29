package com.grishberg.tests.planner;

import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.InstrumentationInfo;
import com.grishberg.tests.planner.parser.InstrumentTestLogParser;
import com.grishberg.tests.planner.parser.TestPlan;
import org.gradle.api.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Provides set of {@link TestPlan} for instrumental tests.
 */
public class InstrumentalTestPlanProvider {
    private final InstrumentationInfo instrumentationInfo;
    private final Map<String, String> instrumentalArgs;
    private final Project project;

    public InstrumentalTestPlanProvider(Project project,
                                        InstrumentationInfo instrumentationInfo,
                                        Map<String, String> instrumentalArgs) {
        this.project = project;
        this.instrumentationInfo = instrumentationInfo;
        this.instrumentalArgs = new HashMap<>(instrumentalArgs);
        this.instrumentalArgs.put("log", "true");
    }

    public Set<TestPlan> provideTestPlan(DeviceWrapper device) {
        InstrumentTestLogParser receiver = new InstrumentTestLogParser();
        StringBuilder command = new StringBuilder("am instrument -r -w");

        instrumentalArgs.put("listener",
                "com.github.grishberg.annotationprinter.AnnotationsTestPrinter");

        for (Map.Entry<String, String> arg : instrumentalArgs.entrySet()) {
            command.append(" -e ");
            command.append(arg.getKey());
            command.append(" ");
            command.append(arg.getValue());
        }
        command.append(" ");
        command.append(instrumentationInfo.getInstrumentalPackage());
        command.append("/");
        command.append(instrumentationInfo.getInstrumentalRunner());
        System.out.println(command.toString());
        try {
            device.executeShellCommand(command.toString(), receiver,
                    0, TimeUnit.SECONDS);
        } catch (Exception e) {
            project.getLogger().error("InstrumentalTestPlanProvider.execute error:", e);
        }
        return receiver.getTestInstances();
    }
}

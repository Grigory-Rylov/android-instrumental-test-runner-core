package com.grishberg.tests.planner;

import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.InstrumentationArgsProvider;
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
    private final Project project;

    public InstrumentalTestPlanProvider(Project project,
                                        InstrumentationInfo instrumentationInfo) {
        this.project = project;
        this.instrumentationInfo = instrumentationInfo;
    }

    public Set<TestPlan> provideTestPlan(DeviceWrapper device,
                                         Map<String, String> instrumentalArgs) {
        HashMap<String, String> args = new HashMap<>(instrumentalArgs);
        args.put("log", "true");

        InstrumentTestLogParser receiver = new InstrumentTestLogParser();
        StringBuilder command = new StringBuilder("am instrument -r -w");

        args.put("listener",
                "com.github.grishberg.annotationprinter.AnnotationsTestPrinter");

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

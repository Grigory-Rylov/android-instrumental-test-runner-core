package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.common.RunnerLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides set of {@link TestPlanElement} for instrumental tests.
 * This class will be thread-safe when instance of {@link RunnerLogger} is thread-safe.
 */
public class InstrumentalTestPlanProvider {
    private static final String TAG = InstrumentalTestPlanProvider.class.getSimpleName();
    private final InstrumentalExtension instrumentationInfo;
    private final Map<String, String> propertiesMap;
    private final PackageTreeGenerator packageTreeGenerator;
    private final RunnerLogger logger;

    public InstrumentalTestPlanProvider(Map<String, String> propertiesMap,
                                        InstrumentalExtension instrumentationInfo,
                                        PackageTreeGenerator packageTreeGenerator,
                                        RunnerLogger logger) {
        this.propertiesMap = Collections.unmodifiableMap(propertiesMap);
        this.instrumentationInfo = new InstrumentalExtension(instrumentationInfo);
        this.packageTreeGenerator = packageTreeGenerator;
        this.logger = logger;
    }

    public List<TestPlanElement> provideTestPlan(ConnectedDeviceWrapper device,
                                                 Map<String, String> instrumentalArgs) throws CommandExecutionException {
        logger.i(TAG, "provideTestPlan for device {}", device.getName());
        HashMap<String, String> args = new HashMap<>(instrumentalArgs);
        args.put("log", "true");

        args.putAll(getArgsFromCli());

        InstrumentTestLogParser receiver = new InstrumentTestLogParser();
        receiver.setLogger(new TestLogParserLogger(logger));
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
            throw new CommandExecutionException(e);
        }

        return receiver.getTestInstances();
    }

    private Map<String, String> getArgsFromCli() {
        HashMap<String, String> result = new HashMap<>();
        if (propertiesMap.get("testClass") != null) {
            Object aClass = propertiesMap.get("testClass");
            result.put("class", (String) aClass);
        } else if (propertiesMap.get("testPackage") != null) {
            Object aPackage = propertiesMap.get("testPackage");
            result.put("package", (String) aPackage);
        }
        return result;
    }

    /**
     * @return test holder contains all test methods in project.
     */
    public InstrumentalTestHolder provideInstrumentalTests(ConnectedDeviceWrapper device,
                                                           Map<String, String> instrumentalArgs) throws CommandExecutionException {
        // TODO: create fabric
        return new InstrumentalTestHolderImpl(provideTestPlan(device, instrumentalArgs), packageTreeGenerator);
    }

    private class TestLogParserLogger implements InstrumentTestLogParser.ParserLogger {
        private final RunnerLogger logger;

        private TestLogParserLogger(RunnerLogger logger) {
            this.logger = logger;
        }

        @Override
        public void logLine(String line) {
            logger.i(TAG, line);
        }
    }
}

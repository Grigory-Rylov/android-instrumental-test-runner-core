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

    public InstrumentalTestPlanProvider(Map<String, String> propertiesMap,
                                        InstrumentalExtension instrumentationInfo,
                                        PackageTreeGenerator packageTreeGenerator) {
        this.propertiesMap = Collections.unmodifiableMap(propertiesMap);
        this.instrumentationInfo = new InstrumentalExtension(instrumentationInfo);
        this.packageTreeGenerator = packageTreeGenerator;
    }

    public List<TestPlanElement> provideTestPlan(ConnectedDeviceWrapper device,
                                                 Map<String, String> instrumentalArgs) throws CommandExecutionException {
        RunnerLogger logger = device.getLogger();
        logger.i(TAG, "Get list of tests in \"{}\" app", instrumentationInfo.getInstrumentalPackage());
        HashMap<String, String> args = new HashMap<>(instrumentalArgs);
        args.put("log", "true");

        args.putAll(getArgsFromCli());

        InstrumentTestLogParser receiver = new InstrumentTestLogParser(logger);
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
            device.executeShellCommand(command.toString(),
                    receiver, instrumentationInfo.getMaxTimeToOutputResponseInSeconds(), TimeUnit.SECONDS);
        } catch (Throwable e) {
            throw new CommandExecutionException(e);
        }
        logger.i(TAG, "Found {} tests in {}",
                receiver.getTestInstances().size(), instrumentationInfo.getInstrumentalPackage());

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
}

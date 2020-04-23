package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.PackageTreeGenerator;

import java.util.Map;

/**
 * Factory for {@link DeviceCommandsRunner} instance.
 */
public class DeviceCommandsRunnerFactory {
    private Map<String, String> propertiesMap;
    private InstrumentalExtension instrumentationInfo;
    private PackageTreeGenerator packageTreeGenerator;

    public DeviceCommandsRunnerFactory(Map<String, String> propertiesMap,
                                       InstrumentalExtension instrumentationInfo,
                                       PackageTreeGenerator packageTreeGenerator) {
        this.propertiesMap = propertiesMap;
        this.instrumentationInfo = instrumentationInfo;
        this.packageTreeGenerator = packageTreeGenerator;
    }

    private InstrumentalTestPlanProvider createInstrumentalTestPlanProvider() {
        return new InstrumentalTestPlanProvider(propertiesMap, instrumentationInfo,
                packageTreeGenerator);
    }

    DeviceCommandsRunner provideDeviceCommandRunner(DeviceRunnerCommandProvider commandProvider) {
        return new SimpleCommandsRunner(createInstrumentalTestPlanProvider(), commandProvider);
    }
}

package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.planner.TestListProvider;
import com.github.grishberg.tests.planner.TestListProviderFromTestApk;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Factory for {@link DeviceCommandsRunner} instance.
 */
public class DeviceCommandsRunnerFactory implements CommandsRunnerFactory {
    private InstrumentalExtension instrumentationInfo;

    public DeviceCommandsRunnerFactory(InstrumentalExtension instrumentationInfo) {
        this.instrumentationInfo = instrumentationInfo;
    }

    private TestListProvider createInstrumentalTestPlanProvider() {
        return new TestListProviderFromTestApk(new File(instrumentationInfo.testApkPath));
    }

    @NotNull
    @Override
    public DeviceCommandsRunner provideDeviceCommandRunner(@NotNull DeviceRunnerCommandProvider commandProvider) {
        return new SimpleCommandsRunner(createInstrumentalTestPlanProvider(), commandProvider);
    }
}

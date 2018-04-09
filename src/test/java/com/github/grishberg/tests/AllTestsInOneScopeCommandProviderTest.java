package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 24.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AllTestsInOneScopeCommandProviderTest {
    private static final HashMap<String, String> ARGS = new HashMap<>();
    AllTestsInOneScopeCommandProvider provider;
    @Mock
    Project project;
    @Mock
    InstrumentalPluginExtension extension;
    @Mock
    InstrumentationArgsProvider argsProvider;
    @Mock
    RunnerLogger logger;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    InstrumentalTestPlanProvider planProvider;
    @Mock
    Environment environment;

    @Before
    public void setUp() throws Exception {
        when(argsProvider.provideInstrumentationArgs(deviceWrapper)).thenReturn(ARGS);
        provider = new AllTestsInOneScopeCommandProvider(project, argsProvider, logger);
    }

    @Test
    public void provideCommandsForDevice() throws Exception {
        List<DeviceRunnerCommand> commandList = provider.provideCommandsForDevice(deviceWrapper,
                planProvider, environment);
        Assert.assertEquals(1, commandList.size());
        DeviceRunnerCommand command = commandList.get(0);
        Assert.assertNotNull(command);
        verify(argsProvider).provideInstrumentationArgs(deviceWrapper);
        verify(logger).i(AllTestsInOneScopeCommandProvider.class.getSimpleName(),
                "device = {}, args = {}", deviceWrapper, ARGS);
    }
}
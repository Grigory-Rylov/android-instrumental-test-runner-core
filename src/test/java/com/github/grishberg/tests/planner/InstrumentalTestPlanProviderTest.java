package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Created by grishberg on 23.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentalTestPlanProviderTest {
    private static final String RUN_LOG_COMMAND = "am instrument -r -w -e log true -e listener test_listener TestAppPackage/TestRunner";
    private InstrumentalTestPlanProvider provider;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    Project project;
    InstrumentalPluginExtension extension = new InstrumentalPluginExtension();
    @Mock
    PackageTreeGenerator treeGenerator;
    @Mock
    RunnerLogger logger;

    @Before
    public void setUp() throws Exception {
        extension.setInstrumentListener("test_listener");
        extension.setInstrumentalRunner("TestRunner");
        extension.setInstrumentalPackage("TestAppPackage");
        provider = new InstrumentalTestPlanProvider(project, extension, treeGenerator, logger);
    }

    @Test
    public void provideTestPlan() throws Exception {
        HashMap<String, String> args = new HashMap<>();

        provider.provideTestPlan(deviceWrapper, args);

        verify(deviceWrapper).executeShellCommand(eq(RUN_LOG_COMMAND), any(InstrumentTestLogParser.class),
                eq(0L), eq(TimeUnit.SECONDS));
    }
}
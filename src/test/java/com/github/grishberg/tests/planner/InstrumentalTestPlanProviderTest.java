package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Created by grishberg on 23.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentalTestPlanProviderTest {
    private static final String RUN_LOG_COMMAND = "am instrument -r -w -e log true -e listener test_listener TestAppPackage/TestRunner";
    private static final String RUN_LOG_COMMAND2 = "am instrument -r -w -e listener test_listener -e log true TestAppPackage/TestRunner";
    private static final String RUN_LOG_COMMAND_WITH_ARG = "am instrument -r -w -e log true -e listener test_listener -e class com.test.SpecialTest TestAppPackage/TestRunner";
    private InstrumentalTestPlanProvider provider;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    InstrumentalExtension extension = new InstrumentalExtension();
    @Mock
    PackageTreeGenerator treeGenerator;
    @Mock
    RunnerLogger logger;
    Map<String, String> paramsMap = new HashMap<>();

    @Before
    public void setUp() {
        extension.setInstrumentListener("test_listener");
        extension.setInstrumentalRunner("TestRunner");
        extension.setInstrumentalPackage("TestAppPackage");
        provider = new InstrumentalTestPlanProvider(paramsMap, extension, treeGenerator, logger);
    }

    @Test
    public void provideTestPlan() throws Exception {
        HashMap<String, String> args = new HashMap<>();

        provider.provideTestPlan(deviceWrapper, args);

        verify(deviceWrapper).executeShellCommand(eq(RUN_LOG_COMMAND), any(InstrumentTestLogParser.class),
                eq(0L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void sendAdditionalArgTestClass() throws Exception {
        paramsMap.put("testClass", "com.test.SpecialTest");
        provider = new InstrumentalTestPlanProvider(paramsMap, extension, treeGenerator, logger);

        HashMap<String, String> args = new HashMap<>();

        provider.provideTestPlan(deviceWrapper, args);

        verify(deviceWrapper).executeShellCommand(eq(RUN_LOG_COMMAND_WITH_ARG), any(InstrumentTestLogParser.class),
                eq(0L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void removeShardArgumentsIfExists() throws Exception {
        HashMap<String, String> args = new HashMap<>();
        args.put("numShards", "2");
        args.put("shardIndex", "0");

        provider.provideTestPlan(deviceWrapper, args);

        verify(deviceWrapper).executeShellCommand(eq(RUN_LOG_COMMAND2), any(InstrumentTestLogParser.class),
                eq(0L), eq(TimeUnit.SECONDS));
    }
}
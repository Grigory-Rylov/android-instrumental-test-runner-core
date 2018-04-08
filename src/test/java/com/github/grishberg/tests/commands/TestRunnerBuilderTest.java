package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.Environment;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 03.04.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestRunnerBuilderTest {
    private static final String TEST_PACKAGE = "com.test.testpackage";
    private static final String RUNNER_NAME = "SampleRunner";
    private InstrumentalPluginExtension extension = new InstrumentalPluginExtension();
    private TestRunnerBuilder builder;
    private Map<String, String> args = new HashMap<>();
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    Project project;
    @Mock
    Environment environment;
    @Mock
    RunnerLogger logger;
    @Mock
    TestRunnerContext context;

    @Before
    public void setUp() throws Exception {
        when(context.getEnvironment()).thenReturn(environment);
        when(context.getInstrumentalInfo()).thenReturn(extension);
        extension.setApplicationId("com.test.packageId");
        extension.setInstrumentalPackage(TEST_PACKAGE);
        extension.setInstrumentalRunner(RUNNER_NAME);
        builder = new TestRunnerBuilder(project, args, deviceWrapper, context);
    }

    @Test
    public void getTestRunner() throws Exception {
        RemoteAndroidTestRunner testRunner = builder.getTestRunner();
        Assert.assertNotNull(testRunner);
        Assert.assertEquals(TEST_PACKAGE, testRunner.getPackageName());
        Assert.assertEquals(RUNNER_NAME, testRunner.getRunnerName());
        testRunner.getAmInstrumentCommand();
        Assert.assertEquals("/data/data/com.test.packageId/coverage.ec", builder.getCoverageFile());
    }

    @Test
    public void getTestRunnerListener() {

        TestXmlReportsGenerator reportsGenerator = builder.getTestRunListener();
        Assert.assertNotNull(reportsGenerator);
    }
}
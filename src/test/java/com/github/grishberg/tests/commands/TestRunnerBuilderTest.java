package com.github.grishberg.tests.commands;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

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

    @Before
    public void setUp() throws Exception {
        extension.setApplicationId("com.test.packageId");
        extension.setInstrumentalPackage(TEST_PACKAGE);
        extension.setInstrumentalRunner(RUNNER_NAME);
        builder = new TestRunnerBuilder(extension, args, deviceWrapper);
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
}
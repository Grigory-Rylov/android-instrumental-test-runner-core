package com.github.grishberg.tests;

import org.gradle.api.Project;
import org.junit.Test;

/**
 * Created by grishberg on 15.10.17.
 */
public class InstrumentalTaskTest extends BaseTestCaseWithLogger {
    private Project project;

    public InstrumentalTaskTest() {
        project = getProject();
        project.getPluginManager().apply(com.github.grishberg.tests.InstrumentalTestPlugin.class);
    }

    @Test
    public void executeTask() throws Exception {
        InstrumentalPluginExtension ext = project.getExtensions().findByType(InstrumentalPluginExtension.class);
        ext.setFlavorName("TEST_FLAVOR");
        ext.setApplicationId("com.grishberg.gpsexample");
        ext.setCoverageEnabled(true);
        ext.setInstrumentalPackage("com.grishberg.gpsexample.test");
        ext.setInstrumentalRunner("android.support.test.runner.AndroidJUnitRunner");

        InstrumentationTestTask task = provideTask();

        task.runTask();
    }

    private InstrumentationTestTask provideTask() {

        return (InstrumentationTestTask) project.getTasks().getByName(InstrumentationTestTask.NAME);
    }
}

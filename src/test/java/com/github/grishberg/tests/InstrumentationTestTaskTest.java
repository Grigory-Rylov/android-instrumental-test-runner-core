package com.github.grishberg.tests;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by grishberg on 28.03.18.
 */
public class InstrumentationTestTaskTest {
    private static final String TEST_DIR = "/test_dir";
    private final Project project = ProjectBuilder.builder().build();
    private InstrumentationTestTask task;
    private InstrumentalPluginExtension ext;

    public InstrumentationTestTaskTest() {
        project.getPluginManager().apply(com.github.grishberg.tests.InstrumentalTestPlugin.class);
        ext = project.getExtensions().findByType(InstrumentalPluginExtension.class);
        task = provideTask();
    }

    private InstrumentationTestTask provideTask() {
        return (InstrumentationTestTask) project.getTasks().getByName(InstrumentationTestTask.NAME);
    }

    @Test
    public void getCoverageDirWhenNotInitiated() throws Exception {
        File dir = task.getCoverageDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/coverage/default_flavor"));
    }

    @Test
    public void getCoverageDirWhenInitiated() throws Exception {
        task.setCoverageDir(new File(TEST_DIR));

        File dir = task.getCoverageDir();

        Assert.assertEquals(TEST_DIR, dir.getAbsolutePath());
    }

    @Test
    public void getCoverageDirWhenExtensionInitiated() throws Exception {
        ext.setFlavorName("custom_flavor");
        task = provideTask();

        File dir = task.getCoverageDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/coverage/custom_flavor"));
    }

    @Test
    public void getResultsDir() throws Exception {
        File dir = task.getResultsDir();
        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/default_flavor"));
    }

    @Test
    public void getResultsDirWhenInitiated() throws Exception {
        task.setResultsDir(new File(TEST_DIR));

        File dir = task.getResultsDir();

        Assert.assertEquals(TEST_DIR, dir.getAbsolutePath());
    }

    @Test
    public void getResultsDirWhenExtensionInitiated() throws Exception {
        ext.setFlavorName("custom_flavor");
        task = provideTask();

        File dir = task.getResultsDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/custom_flavor"));
    }

    @Test
    public void getReportsDir() throws Exception {
        File dir = task.getReportsDir();
        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/default_flavor"));
    }

    @Test
    public void getReportsDirWhenInitiated() throws Exception {
        task.setReportsDir(new File(TEST_DIR));
        File dir = task.getReportsDir();

        Assert.assertEquals(TEST_DIR, dir.getAbsolutePath());
    }

    @Test
    public void getReportsDirWhenExtensionInitiated() throws Exception {
        ext.setFlavorName("custom_flavor");
        task = provideTask();

        File dir = task.getReportsDir();

        Assert.assertTrue(dir.getAbsolutePath().endsWith("androidTest/custom_flavor"));
    }

}
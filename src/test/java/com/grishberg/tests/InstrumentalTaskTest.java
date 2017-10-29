package com.grishberg.tests;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.internal.logging.sink.OutputEventRenderer;
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger;
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * Created by grishberg on 15.10.17.
 */
public class InstrumentalTaskTest {
    public InstrumentalTaskTest() {
        initLogger();
    }

    private void initLogger() {
        OutputEventListenerBackedLoggerContext loggerFactory = (OutputEventListenerBackedLoggerContext) LoggerFactory.getILoggerFactory();
        loggerFactory.setLevel(LogLevel.INFO);
        OutputEventRenderer outputEventListener = (OutputEventRenderer) loggerFactory.getOutputEventListener();
        outputEventListener.configure(LogLevel.INFO);
    }

    @Test(expected = RuntimeException.class)
    public void failWithoutSetInstrumentalInfo() throws Exception {
        InstrumentalTestTask task = provideTask();
        task.runTask();
    }

    @Test
    public void executeTask() throws Exception {
        InstrumentalTestTask task = provideTask();
        task.setInstrumentationInfo(new InstrumentationInfo.Builder("com.grishberg.gpsexample",
                "com.grishberg.gpsexample.test",
                "android.support.test.runner.AndroidJUnitRunner")
                .setFlavorName("TEST_FLAVOR")
                .build());
        task.runTask();
    }

    private InstrumentalTestTask provideTask() {
        Project project = ProjectBuilder.builder().build();
        OutputEventListenerBackedLogger logger = (OutputEventListenerBackedLogger) project.getLogger();

        project.getPluginManager().apply(com.grishberg.tests.InstrumentalTestPlugin.class);
        return (InstrumentalTestTask) project.getTasks().getByName(InstrumentalTestTask.TASK_NAME);
    }
}

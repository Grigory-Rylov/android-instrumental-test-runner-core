package com.github.grishberg.tests;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.internal.logging.sink.OutputEventRenderer;
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext;
import org.gradle.testfixtures.ProjectBuilder;
import org.slf4j.LoggerFactory;

/**
 * Created by grishberg on 02.02.18.
 */
public class BaseTestCaseWithLogger {
    private final Project project;

    public BaseTestCaseWithLogger() {
        initLogger();
        project = ProjectBuilder.builder().build();
    }

    private void initLogger() {
        OutputEventListenerBackedLoggerContext loggerFactory = (OutputEventListenerBackedLoggerContext) LoggerFactory.getILoggerFactory();
        loggerFactory.setLevel(LogLevel.INFO);
        OutputEventRenderer outputEventListener = (OutputEventRenderer) loggerFactory.getOutputEventListener();
        outputEventListener.configure(LogLevel.INFO);
    }

    protected Logger getLogger() {
        return project.getLogger();
    }

    protected Project getProject() {
        return project;
    }
}

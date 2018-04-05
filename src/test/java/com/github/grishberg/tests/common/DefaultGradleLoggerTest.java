package com.github.grishberg.tests.common;

import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.internal.logging.sink.OutputEventRenderer;
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLoggerContext;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * Created by grishberg on 22.03.18.
 */
public class DefaultGradleLoggerTest {
    private static final String TAG = "TAG";
    private Project project = ProjectBuilder.builder().build();
    private DefaultGradleLogger logger = new DefaultGradleLogger(project.getLogger());

    public DefaultGradleLoggerTest() {
        initLogger();
    }

    private void initLogger() {
        OutputEventListenerBackedLoggerContext loggerFactory = (OutputEventListenerBackedLoggerContext) LoggerFactory.getILoggerFactory();
        loggerFactory.setLevel(LogLevel.DEBUG);
        OutputEventRenderer outputEventListener = (OutputEventRenderer) loggerFactory.getOutputEventListener();
        outputEventListener.configure(LogLevel.DEBUG);
    }

    @Test
    public void w() throws Exception {
        logger.w(TAG, "msg");
        logger.w(TAG, null);
    }

    @Test
    public void i() throws Exception {
        logger.i(TAG, "msg");
        logger.i(TAG, null);
        logger.i(TAG, "format msg {} {}", 1, "value");
        logger.i(TAG, "format msg %s %s", 1, "value");
        logger.i(TAG, null, 1, "value");
    }

    @Test
    public void d() throws Exception {
        logger.d(TAG, "msg");
        logger.d(TAG, null);
        logger.d(TAG, "format msg {} {}", 1, "value");
        logger.d(TAG, "format msg %s %s", 1, "value");
        logger.d(TAG, null, 1, "value");
    }

    @Test
    public void e() throws Exception {
        logger.e(TAG, "error msg");
        logger.e(TAG, null);
        logger.e(TAG, "error msg", new Throwable());
    }
}
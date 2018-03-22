package com.github.grishberg.tests.common;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

/**
 * Created by grishberg on 22.03.18.
 */
public class DefaultGradleLoggerTest {
    private static final String TAG = "TAG";
    private Project project = ProjectBuilder.builder().build();
    private DefaultGradleLogger logger = new DefaultGradleLogger(project.getLogger());

    @Test
    public void w() throws Exception {
        logger.w(TAG, "message");
        logger.w(TAG, null);
    }

    @Test
    public void i() throws Exception {
        logger.i(TAG, "messag");
        logger.i(TAG, null);
        logger.i(TAG, "format msg {} {}", 1, "value");
        logger.i(TAG, null, 1, "value");
    }

    @Test
    public void d() throws Exception {
        logger.d(TAG, "messag");
        logger.d(TAG, null);
        logger.d(TAG, "format msg {} {}", 1, "value");
        logger.d(TAG, null, 1, "value");
    }

    @Test
    public void e() throws Exception {
        logger.e(TAG, "error msg");
        logger.e(TAG, null);
        logger.e(TAG, "error msg", new Throwable());
    }
}
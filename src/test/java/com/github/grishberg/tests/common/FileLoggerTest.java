package com.github.grishberg.tests.common;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

/**
 * Created by grishberg on 22.03.18.
 */
public class FileLoggerTest {
    private static final String TAG = "TAG";
    private Project project = ProjectBuilder.builder().build();
    private FileLogger logger = new FileLogger(project, "test.log");

    @Test
    public void w() throws Exception {
        logger.w(TAG, "msg");
        logger.w(TAG, null);
    }

    @Test
    public void i() throws Exception {
        logger.i(TAG, "msg");
        logger.i(TAG, null);
        logger.i(TAG, null, 1, "2", 100L);
        logger.i(TAG, "msg %s %s %s", 1, "2", 100L);
        logger.i(TAG, "msg {0} {1} {2}", 1, "2", 100L);
    }

    @Test
    public void d() throws Exception {
        logger.d(TAG, "msg");
        logger.d(TAG, null);
        logger.d(TAG, null, 1, "2", 100L);
        logger.d(TAG, "msg %s %s %s", 1, "2", 100L);
        logger.d(TAG, "msg {0} {1} {2}", 1, "2", 100L);
    }

    @Test
    public void e() throws Exception {
        logger.e(TAG, "msg");
        logger.e(TAG, "msg", new Throwable());
    }
}
package com.github.grishberg.tests.commands.reports;

/**
 * Created by grishberg on 06.04.18.
 */
public interface LogcatSaver {
    void clearLogcat();

    void saveLogcat(String testName);
}

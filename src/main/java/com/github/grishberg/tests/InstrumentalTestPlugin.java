package com.github.grishberg.tests;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * Plugin for testing.
 */
public class InstrumentalTestPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getLogger().info("InstrumentalTestPlugin applied");
        Task task = project.getTasks()
                .create(InstrumentalTestTask.NAME, InstrumentalTestTask.class);
        task.setGroup("verification");
        task.setDescription("Plugin for running instrumental tests on multiple devices");
    }
}

package com.github.grishberg.tests

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for testing.
 */
class InstrumentalTestPlugin implements Plugin<Project> {

    public static final String INSTRUMENTAL_PLUGIN_EXTENSION = 'instrumentalPluginConfig'

    @Override
    void apply(Project project) {
        project.getLogger().info("InstrumentalTestPlugin apply")

        project.extensions.create(INSTRUMENTAL_PLUGIN_EXTENSION, InstrumentalPluginExtension)

        InstrumentationTestTask task = project.getTasks()
                .create(InstrumentationTestTask.NAME, InstrumentationTestTask.class)

        task.setGroup("verification")
        task.setDescription("Plugin for running instrumental tests on multiple devices")
    }
}

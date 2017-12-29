package com.github.grishberg.tests

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin for testing.
 */
class InstrumentalTestPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.getLogger().info("InstrumentalTestPlugin apply")

        InstrumentalPluginExtension extension = project.extensions.create(
                'instrumentalPluginConfig', InstrumentalPluginExtension)

        InstrumentalTestTask task = project.getTasks()
                .create(InstrumentalTestTask.NAME, InstrumentalTestTask.class)
        task.androidSdkPath = extension.androidSdkPath

        task.setGroup("verification")
        task.setDescription("Plugin for running instrumental tests on multiple devices")
    }
}

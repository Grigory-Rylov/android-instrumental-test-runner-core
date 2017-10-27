package com.grishberg.tests

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by grishberg on 14.10.17.
 */
class InstrumentalTestPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.logger.info("InstrumentalTestPlugin applied")
        def task = project.tasks.create(InstrumentalTestTask.TASK_NAME, InstrumentalTestTask) {}
        task.group = "myGroup"
        task.description = "some description"
    }
}

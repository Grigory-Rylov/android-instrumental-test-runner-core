package com.github.grishberg.tests

import com.android.build.gradle.internal.variant.ApplicationVariantData
import com.android.build.gradle.internal.variant.TestVariantData
import com.android.builder.core.VariantType
import com.github.grishberg.tests.commands.InstallApkCommand
import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * Provides install command list.
 */
class InstallApkCommandsProvider {

    private Project project

    InstallApkCommandsProvider(Project project) {
        this.project = project
    }

    List<InstallApkCommand> provideInstallApkCommands(String buildVariant) {
        Logger logger = project.logger
        ArrayList<InstallApkCommand> prepareCommands = new ArrayList<>(2)

        project.android.applicationVariants.all { variant ->
            if (variant.name == buildVariant) {
                File testedApk = null
                File testApk = null

                def buildDir = project.getBuildDir()
                File apkDir = new File(buildDir, "outputs/apk")

                ApplicationVariantData variantData = variant.variantData
                def apkData = variantData.getOutputScope().getApkDatas().get(0)
                String apkFullName = "${apkData.fullName}/${apkData.getOutputFileName()}"
                testedApk = new File(apkDir, apkFullName)
                logger.info("InstallApkCommandsProvider: apkFile {} exists: {}",
                        apkFullName, testedApk.exists())
                if (testedApk.exists()) {
                    prepareCommands.add(new InstallApkCommand(logger, testedApk))
                }

                TestVariantData testData = variantData.getTestVariantData(VariantType.ANDROID_TEST)
                if (testData != null) {
                    def testApkData = testData.getOutputScope().getApkDatas().get(0)
                    String testApkFullName = "androidTest/${apkData.fullName}/${testApkData.getOutputFileName()}"
                    testApk = new File(apkDir, testApkFullName)
                    logger.info("InstallApkCommandsProvider testApkFile {} exists: {}",
                            testApkFullName, testApk.exists())
                    if (testApk.exists()) {
                        prepareCommands.add(new InstallApkCommand(logger, testApk))
                    }
                }
            }
        }
        return prepareCommands
    }
}

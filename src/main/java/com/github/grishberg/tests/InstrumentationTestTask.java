package com.github.grishberg.tests;

import com.android.build.gradle.internal.test.report.ReportType;
import com.android.build.gradle.internal.test.report.TestReport;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.utils.FileUtils;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Nullable;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.logging.ConsoleRenderer;

import java.io.File;
import java.io.IOException;

/**
 * Main task for running instrumental tests.
 */
public class InstrumentationTestTask extends DefaultTask {
    public static final String NAME = "instrumentalTests";
    private static final int ADB_TIMEOUT = 10;
    private static final int ONE_SECOND = 1000;
    @Nullable
    private String androidSdkPath;
    private File coverageDir;
    private File resultsDir;
    private File reportsDir;
    private DeviceRunnerCommandProvider commandProvider;
    private InstrumentationArgsProvider instrumentationArgsProvider;
    private InstrumentalPluginExtension instrumentationInfo;
    private CommandsForAnnotationProvider commandsForAnnotationProvider;
    private Logger logger;

    public InstrumentationTestTask() {
        logger = getLogger();
        coverageDir = new File(getProject().getBuildDir(), "outputs/androidTest/coverage/");
        reportsDir = new File(getProject().getBuildDir(), "outputs/reports/androidTest/");
        resultsDir = new File(getProject().getBuildDir(), "outputs/androidTest/");
    }

    @TaskAction
    public void runTask() throws InterruptedException, IOException {
        logger.info("InstrumentationTestTask.runTask");
        instrumentationInfo = getProject().getExtensions()
                .findByType(InstrumentalPluginExtension.class);
        androidSdkPath = instrumentationInfo.getAndroidSdkPath();
        try {
            init();

            prepareOutputFolders();

            AndroidDebugBridge adb = AndroidDebugBridge
                    .createBridge(androidSdkPath + "/platform-tools/adb", false);
            waitForAdb(adb);

            InstrumentalTestPlanProvider testPlanProvider = new InstrumentalTestPlanProvider(
                    getProject(), instrumentationInfo);

            Environment environment = new Environment(resultsDir,
                    reportsDir, coverageDir);
            DeviceCommandsRunner runner = new DeviceCommandsRunner(testPlanProvider, commandProvider,
                    environment, logger);

            generateHtmlReport(runner.runCommands(provideDevices(adb)));
        } finally {
            terminate();
        }
    }

    private void prepareOutputFolders() throws IOException {
        cleanFolder(reportsDir);
        cleanFolder(resultsDir);
        cleanFolder(coverageDir);
    }

    private static void cleanFolder(File dir) throws IOException {
        org.apache.commons.io.FileUtils.deleteQuietly(dir);
        if (!dir.mkdirs()) {
            throw new IOException("Cant create folder " + dir.getAbsolutePath());
        }
    }

    private void generateHtmlReport(boolean success) throws IOException {
        FileUtils.cleanOutputDir(reportsDir);
        TestReport report = new TestReport(ReportType.SINGLE_FLAVOR, getResultsDir(), reportsDir);
        report.generateReport();
        if (!success) {
            String reportUrl = (new ConsoleRenderer())
                    .asClickableFileUrl(new File(reportsDir, "index.html"));
            String message = String.format("There were failing tests. See the report at: %s",
                    reportUrl);
            throw new GradleException(message);
        }
    }

    private ConnectedDeviceWrapper[] provideDevices(AndroidDebugBridge adb) {
        IDevice[] devices = adb.getDevices();
        ConnectedDeviceWrapper[] deviceWrappers = new ConnectedDeviceWrapper[devices.length];
        for (int i = 0; i < devices.length; i++) {
            deviceWrappers[i] = new ConnectedDeviceWrapper(devices[i]);
        }
        return deviceWrappers;
    }

    private void terminate() {

    }

    private void init() {
        AndroidDebugBridge.initIfNeeded(false);
        if (androidSdkPath == null) {
            logger.info("androidSdkPath is empty, get path from env ANDROID_HOME");
            androidSdkPath = System.getenv("ANDROID_HOME");
            logger.info("androidSdkPath = {}", androidSdkPath);
        }
        if (instrumentationInfo == null) {
            throw new RuntimeException("Need to set InstrumentationInfo");
        }
        if (commandsForAnnotationProvider == null) {
            commandsForAnnotationProvider = new DefaultCommandsForAnnotationProvider(getLogger(),
                    instrumentationInfo);

            logger.info("Init: commandsForAnnotationProvider is empty, use DefaultCommandsForAnnotationProvider");
        }
        if (instrumentationArgsProvider == null) {
            instrumentationArgsProvider = new DefaultInstrumentationArgsProvider();
            logger.info("init: instrumentationArgsProvider is empty, use DefaultInstrumentationArgsProvider");
        }
        if (commandProvider == null) {
            logger.info("command provider is empty, use DefaultCommandProvider");
            commandProvider = new DefaultCommandProvider(getProject(),
                    instrumentationInfo,
                    instrumentationArgsProvider, commandsForAnnotationProvider);
        }
        coverageDir = new File(getProject().getBuildDir(),
                String.format("outputs/androidTest/coverage/%s", instrumentationInfo.getFlavorName()));
        reportsDir = new File(getProject().getBuildDir(),
                String.format("outputs/reports/androidTest/%s", instrumentationInfo.getFlavorName()));
        resultsDir = new File(getProject().getBuildDir(),
                String.format("outputs/androidTest/%s", instrumentationInfo.getFlavorName()));
    }

    private void waitForAdb(AndroidDebugBridge adb) throws InterruptedException {
        for (int counter = 0; counter < ADB_TIMEOUT; counter++) {
            if (adb.isConnected()) {
                break;
            }
            Thread.sleep(ONE_SECOND);
        }
    }

    @Input
    public void setInstrumentationInfo(InstrumentalPluginExtension instrumentationInfo) {
        this.instrumentationInfo = instrumentationInfo;
    }

    @Input
    public void setInstrumentationArgsProvider(InstrumentationArgsProvider argsProvider) {
        this.instrumentationArgsProvider = argsProvider;
    }

    @Input
    public void setCommandsForAnnotationProvider(CommandsForAnnotationProvider commandsProvider) {
        this.commandsForAnnotationProvider = commandsProvider;
    }

    @Input
    public void setCommandProvider(DeviceRunnerCommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    @Input
    public void setCoverageDir(File coverageDir) {
        this.coverageDir = coverageDir;
    }

    @Input
    public void setResultsDir(File resultsDir) {
        this.resultsDir = resultsDir;
    }

    @Input
    public void setReportsDir(File reportsDir) {
        this.reportsDir = reportsDir;
    }

    public File getCoverageDir() {
        return coverageDir;
    }

    public File getResultsDir() {
        return resultsDir;
    }

    @OutputDirectory
    public File getReportsDir() {
        return reportsDir;
    }
}

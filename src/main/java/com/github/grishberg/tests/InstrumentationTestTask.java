package com.github.grishberg.tests;

import com.android.build.gradle.internal.test.report.ReportType;
import com.android.build.gradle.internal.test.report.TestReport;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.utils.FileUtils;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.common.DefaultGradleLogger;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.PackageTreeGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Nullable;
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
    private static final String TAG = InstrumentationTestTask.class.getSimpleName();
    private static final String DEFAULT_FLAVOR = "default_flavor";
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
    private RunnerLogger logger;

    public InstrumentationTestTask() {
        logger = new DefaultGradleLogger(getLogger());
        instrumentationInfo = getProject().getExtensions()
                .findByType(InstrumentalPluginExtension.class);
    }

    @TaskAction
    public void runTask() throws InterruptedException, IOException {
        logger.i(TAG, "InstrumentationTestTask.runTask");

        androidSdkPath = instrumentationInfo.getAndroidSdkPath();

        init();

        prepareOutputFolders();

        AndroidDebugBridge adb = AndroidDebugBridge
                .createBridge(androidSdkPath + "/platform-tools/adb", false);
        waitForAdb(adb);

        PackageTreeGenerator packageTreeGenerator = new PackageTreeGenerator();
        InstrumentalTestPlanProvider testPlanProvider = new InstrumentalTestPlanProvider(
                getProject(),
                instrumentationInfo, packageTreeGenerator, logger);

        Environment environment = new Environment(resultsDir,
                reportsDir, coverageDir);
        DeviceCommandsRunner runner = new DeviceCommandsRunner(testPlanProvider, commandProvider,
                environment, logger);

        generateHtmlReport(runner.runCommands(provideDevices(adb)));
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

    private void init() {
        AndroidDebugBridge.initIfNeeded(false);
        if (androidSdkPath == null) {
            logger.i(TAG, "androidSdkPath is empty, get path from env ANDROID_HOME");
            androidSdkPath = System.getenv("ANDROID_HOME");
            logger.i(TAG, "androidSdkPath = %s", androidSdkPath);
        }
        if (instrumentationInfo == null) {
            throw new GradleException("Need to set InstrumentationInfo");
        }
        if (commandsForAnnotationProvider == null) {
            commandsForAnnotationProvider = new DefaultCommandsForAnnotationProvider(getLogger(),
                    instrumentationInfo);

            logger.i(TAG, "Init: commandsForAnnotationProvider is empty, use DefaultCommandsForAnnotationProvider");
        }
        if (instrumentationArgsProvider == null) {
            instrumentationArgsProvider = new DefaultInstrumentationArgsProvider();
            logger.i(TAG, "init: instrumentationArgsProvider is empty, use DefaultInstrumentationArgsProvider");
        }
        if (commandProvider == null) {
            logger.i(TAG, "command provider is empty, use DefaultCommandProvider");
            commandProvider = new DefaultCommandProvider(getProject(),
                    instrumentationInfo,
                    instrumentationArgsProvider, commandsForAnnotationProvider, logger);
        }
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
        if (coverageDir == null) {
            String flavor = instrumentationInfo.getFlavorName() != null ?
                    instrumentationInfo.getFlavorName() : DEFAULT_FLAVOR;
            coverageDir = new File(getProject().getBuildDir(),
                    String.format("outputs/androidTest/coverage/%s", flavor));
            logger.d(TAG, "Coverage dir is empty, generate default value %s", coverageDir);
        }
        return coverageDir;
    }

    public File getResultsDir() {
        if (resultsDir == null) {
            String flavor = instrumentationInfo.getFlavorName() != null ?
                    instrumentationInfo.getFlavorName() : DEFAULT_FLAVOR;
            resultsDir = new File(getProject().getBuildDir(),
                    String.format("outputs/androidTest/%s", flavor));
            logger.d(TAG, "Results dir is empty, generate default value %s", resultsDir);
        }
        return resultsDir;
    }

    @OutputDirectory
    public File getReportsDir() {
        if (reportsDir == null) {
            String flavor = instrumentationInfo.getFlavorName() != null ?
                    instrumentationInfo.getFlavorName() : DEFAULT_FLAVOR;
            reportsDir = new File(getProject().getBuildDir(),
                    String.format("outputs/reports/androidTest/%s", flavor));
            logger.d(TAG, "Reports dir is empty, generate default value %s", reportsDir);
        }
        return reportsDir;
    }

    public void setRunnerLogger(RunnerLogger logger) {
        this.logger = logger;
    }
}

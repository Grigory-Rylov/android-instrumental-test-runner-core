package com.github.grishberg.tests;

import com.android.build.gradle.internal.test.report.ReportType;
import com.android.build.gradle.internal.test.report.TestReport;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.utils.FileUtils;
import com.github.grishberg.tests.commands.DeviceCommandProvider;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.logging.ConsoleRenderer;

import java.io.File;
import java.io.IOException;

/**
 * Main task for running instrumental tests.
 */
public class InstrumentalTestTask extends DefaultTask {
    public static final String NAME = "instrumentalTests";
    private static final int ADB_TIMEOUT = 10;
    private static final int ONE_SECOND = 1000;
    private File coverageFilesDir;
    private File testResultsDir;
    private File reportsDir;
    private DeviceCommandProvider commandProvider;
    private InstrumentationArgsProvider instrumentationArgsProvider;
    private InstrumentationInfo instrumentationInfo;
    private CommandsForAnnotationProvider commandsForAnnotationProvider;

    public InstrumentalTestTask() {
    }

    @TaskAction
    public void runTask() throws InterruptedException, IOException {
        getProject().getLogger().debug("InstrumentalTestTask.runTask");
        try {
            init();

            AndroidDebugBridge adb = AndroidDebugBridge.createBridge();
            waitForAdb(adb);

            InstrumentalTestPlanProvider testPlanProvider = new InstrumentalTestPlanProvider(
                    getProject(), instrumentationInfo);
            DeviceCommandsRunner runner = new DeviceCommandsRunner(testPlanProvider, commandProvider, getLogger());
            ;
            generateHtmlReport(runner.runCommands(provideDevices(adb)));
        } finally {
            terminate();
        }
    }

    private void generateHtmlReport(boolean success) throws IOException {
        FileUtils.cleanOutputDir(reportsDir);
        TestReport report = new TestReport(ReportType.SINGLE_FLAVOR, getTestResultsDir(), reportsDir);
        report.generateReport();
        if (!success) {
            String reportUrl = (new ConsoleRenderer())
                    .asClickableFileUrl(new File(reportsDir, "index.html"));
            String message = String.format("There were failing tests. See the report at: %s",
                    reportUrl);
            throw new GradleException(message);
        }
    }

    private DeviceWrapper[] provideDevices(AndroidDebugBridge adb) {
        IDevice[] devices = adb.getDevices();
        DeviceWrapper[] deviceWrappers = new DeviceWrapper[devices.length];
        for (int i = 0; i < devices.length; i++) {
            deviceWrappers[i] = new DeviceWrapper(devices[i]);
        }
        return deviceWrappers;
    }

    private void terminate() {

    }

    private void init() {
        AndroidDebugBridge.initIfNeeded(false);
        if (instrumentationInfo == null) {
            throw new RuntimeException("Need to set InstrumentationInfo");
        }
        if (commandsForAnnotationProvider == null) {
            commandsForAnnotationProvider = new DefaultCommandsForAnnotationProvider(getLogger(),
                    instrumentationInfo);
            getLogger().info("init: commandsForAnnotationProvider is empty, use DefaultCommandsForAnnotationProvider");
        }
        if (instrumentationArgsProvider == null) {
            instrumentationArgsProvider = new DefaultInstrumentationArgsProvider();
            getLogger().info("init: instrumentationArgsProvider is empty, use DefaultInstrumentationArgsProvider");
        }
        if (commandProvider == null) {
            getProject().getLogger()
                    .info("command provider is empty, use DefaultCommandProvider");
            commandProvider = new DefaultCommandProvider(getProject(),
                    instrumentationInfo,
                    instrumentationArgsProvider, commandsForAnnotationProvider);
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
    public void setInstrumentationInfo(InstrumentationInfo instrumentationInfo) {
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
    public void setCommandProvider(DeviceCommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    public File getCoverageFilesDir() {
        return coverageFilesDir;
    }

    public File getTestResultsDir() {
        return testResultsDir;
    }

    public File getReportsDir() {
        return reportsDir;
    }

    public void setCoverageFilesDir(File coverageFilesDir) {
        this.coverageFilesDir = coverageFilesDir;
    }

    public void setTestResultsDir(File testResultsDir) {
        this.testResultsDir = testResultsDir;
    }

    public void setReportsDir(File reportsDir) {
        this.reportsDir = reportsDir;
    }
}

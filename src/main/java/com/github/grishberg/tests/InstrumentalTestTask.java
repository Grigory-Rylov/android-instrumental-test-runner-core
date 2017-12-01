package com.github.grishberg.tests;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.grishberg.tests.commands.DeviceCommandProvider;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 * Main task for running instrumental tests.
 */
public class InstrumentalTestTask extends DefaultTask {
    public static final String TASK_NAME = "instrumentalTests";
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
    public void runTask() throws InterruptedException {
        getProject().getLogger().debug("InstrumentalTestTask.runTask");
        try {
            init();

            AndroidDebugBridge adb = AndroidDebugBridge.createBridge();
            waitForAdb(adb);

            InstrumentalTestPlanProvider testPlanProvider = new InstrumentalTestPlanProvider(
                    getProject(), instrumentationInfo);
            DeviceCommandsRunner runner = new DeviceCommandsRunner(testPlanProvider, commandProvider, getLogger());
            runner.runCommands(provideDevices(adb));
        } finally {
            terminate();
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

    public void setInstrumentationInfo(InstrumentationInfo instrumentationInfo) {
        this.instrumentationInfo = instrumentationInfo;
    }

    public void setInstrumentationArgsProvider(InstrumentationArgsProvider argsProvider) {
        this.instrumentationArgsProvider = argsProvider;
    }

    public void setCommandsForAnnotationProvider(CommandsForAnnotationProvider commandsProvider) {
        this.commandsForAnnotationProvider = commandsProvider;
    }
}

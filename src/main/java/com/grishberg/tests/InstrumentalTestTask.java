package com.grishberg.tests;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.grishberg.tests.commands.DeviceCommandProvider;
import com.grishberg.tests.planner.InstrumentalTestPlanProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 * Created by grishberg on 14.10.17.
 */
public class InstrumentalTestTask extends DefaultTask {
    public static final String TASK_NAME = "instrumentalTests";
    private File coverageFilesDir;
    private File testResultsDir;
    private File reportsDir;
    private DeviceCommandProvider commandProvider;
    private InstrumentationArgsProvider instrumentationArgsProvider;
    private InstrumentationInfo instrumentationInfo;

    public InstrumentalTestTask() {
    }

    @TaskAction
    public void runTask() throws InterruptedException {
        getProject().getLogger().debug("InstrumentalTestTask.runTask");
        try {
            init();

            AndroidDebugBridge adb = AndroidDebugBridge.createBridge();
            waitForAdb();

            InstrumentalTestPlanProvider testPlanProvider = new InstrumentalTestPlanProvider(getProject(), instrumentationInfo, instrumentationArgsProvider.provideInstrumentationArgs());
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
        if (instrumentationArgsProvider == null) {
            instrumentationArgsProvider = new DefaultInstrumentationArgsProvider();
        }
        if (commandProvider == null) {
            getProject().getLogger()
                    .info("command provider not set, use DefaultCommandProvider");
            commandProvider = new DefaultCommandProvider(getProject(),
                    instrumentationInfo,
                    instrumentationArgsProvider);
        }
    }

    private static void waitForAdb() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setInstrumentationInfo(InstrumentationInfo instrumentationInfo) {
        this.instrumentationInfo = instrumentationInfo;
    }

    public void setInstrumentationArgsProvider(InstrumentationArgsProvider argsProvider) {
        this.instrumentationArgsProvider = argsProvider;
    }
}

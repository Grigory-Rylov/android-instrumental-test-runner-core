package com.github.grishberg.tests;

import com.android.ddmlib.AndroidDebugBridge;
import com.github.grishberg.tests.adb.AdbWrapper;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider;
import com.github.grishberg.tests.common.BuildFileSystem;
import com.github.grishberg.tests.common.BuildFileSystemImpl;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.sharding.DefaultDeviceTypeAdapter;
import com.github.grishberg.tests.sharding.DeviceTypeAdapter;
import com.github.grishberg.tests.sharding.ShardArgumentsImpl;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main task for running instrumental tests.
 */
public class InstrumentationTestLauncher {
    private static final String TAG = InstrumentationTestLauncher.class.getSimpleName();
    private static final String DEFAULT_FLAVOR = "default_flavor";
    @Nullable
    private String androidSdkPath;
    private File coverageDir;
    private File resultsDir;
    private File reportsDir;
    private DeviceRunnerCommandProvider commandProvider;
    private InstrumentationArgsProvider instrumentationArgsProvider;
    private InstrumentalExtension instrumentationInfo;
    private CommandsForAnnotationProvider commandsForAnnotationProvider;
    private DeviceCommandsRunnerFabric deviceCommandsRunnerFabric;
    private AdbWrapper adbWrapper;
    private RunnerLogger logger;
    private String projectName;
    private String buildDir;
    private DeviceTypeAdapter deviceTypeAdapter;
    private BuildFileSystem buildFileSystem;
    private HashMap<String, String> screenshotRelations = new HashMap<>();
    private ProcessCrashHandler processCrashedHandler;

    public InstrumentationTestLauncher(String projectName,
                                       String buildDir,
                                       InstrumentalExtension instrumentalExtension,
                                       AdbWrapper adbWrapper,
                                       DeviceCommandsRunnerFabric deviceCommandsRunnerFabric,
                                       BuildFileSystem buildFileSystem,
                                       RunnerLogger logger) {
        this.projectName = projectName;
        this.buildDir = buildDir;
        instrumentationInfo = new InstrumentalExtension(instrumentalExtension);
        this.adbWrapper = adbWrapper;
        this.deviceCommandsRunnerFabric = deviceCommandsRunnerFabric;
        this.logger = logger;
        this.buildFileSystem = buildFileSystem;
    }

    public InstrumentationTestLauncher(String projectName,
                                       String buildDir,
                                       InstrumentalExtension instrumentalExtension,
                                       AdbWrapper adbWrapper,
                                       DeviceCommandsRunnerFabric deviceCommandsRunnerFabric,
                                       RunnerLogger logger) {
        this(projectName, buildDir, instrumentalExtension, adbWrapper,
                deviceCommandsRunnerFabric, new BuildFileSystemImpl(), logger);
    }

    void initAfterApply(AdbWrapper adbWrapper,
                        DeviceCommandsRunnerFabric deviceCommandsRunnerFabric,
                        RunnerLogger logger) {
        this.adbWrapper = adbWrapper;
        this.deviceCommandsRunnerFabric = deviceCommandsRunnerFabric;
        this.logger = logger;
    }

    /**
     * Launches tests.
     *
     * @throws InterruptedException
     * @throws IOException
     * @throws CommandExecutionException
     */
    public boolean launchTests() throws InterruptedException, IOException, CommandExecutionException {
        logger.i(TAG, "InstrumentationTestLauncher.launchTests");

        screenshotRelations.clear();
        androidSdkPath = instrumentationInfo.getAndroidSdkPath();
        init();
        adbWrapper.init(androidSdkPath, logger);
        prepareOutputFolders();
        adbWrapper.waitForAdb();

        Environment environment = new Environment(getResultsDir(),
                getReportsDir(), getCoverageDir());
        DeviceCommandsRunner runner = deviceCommandsRunnerFabric.provideDeviceCommandRunner(commandProvider);

        TestRunnerContext context = new TestRunnerContextImpl(instrumentationInfo,
                environment, screenshotRelations, logger);
        if (processCrashedHandler != null) {
            context.setProcessCrashHandler(processCrashedHandler);
        }
        return runner.runCommands(getDeviceList(), context);
    }

    private void init() {
        AndroidDebugBridge.initIfNeeded(false);
        if (androidSdkPath == null) {
            logger.i(TAG, "androidSdkPath is empty, get path from env ANDROID_HOME");
            androidSdkPath = System.getenv("ANDROID_HOME");
            logger.i(TAG, "androidSdkPath = {}", androidSdkPath);
        }
        if (instrumentationInfo == null) {
            throw new RuntimeException("Need to set InstrumentationInfo");
        }
        if (commandsForAnnotationProvider == null) {
            commandsForAnnotationProvider = new DefaultCommandsForAnnotationProvider();

            logger.i(TAG, "Init: commandsForAnnotationProvider is empty, use DefaultCommandsForAnnotationProvider");
        }
        if (instrumentationArgsProvider == null) {
            if (deviceTypeAdapter == null) {
                deviceTypeAdapter = new DefaultDeviceTypeAdapter();
            }
            instrumentationArgsProvider = new DefaultInstrumentationArgsProvider(
                    instrumentationInfo, new ShardArgumentsImpl(adbWrapper, logger, deviceTypeAdapter));
            logger.i(TAG, "init: instrumentationArgsProvider is empty, use DefaultInstrumentationArgsProvider");
        }
        if (commandProvider == null) {
            logger.i(TAG, "command provider is empty, use DefaultCommandProvider");
            commandProvider = new DefaultCommandProvider(projectName,
                    instrumentationArgsProvider, commandsForAnnotationProvider);
        }
    }

    private void prepareOutputFolders() throws IOException {
        buildFileSystem.cleanFolder(getReportsDir());
        buildFileSystem.cleanFolder(getResultsDir());
        buildFileSystem.cleanFolder(getCoverageDir());
    }

    /**
     * @return path relations between screenshots and failed tests.
     */
    public Map<String, String> getScreenshotRelations() {
        return screenshotRelations;
    }

    /**
     * @return List of available devices.
     */
    public List<ConnectedDeviceWrapper> getDeviceList() {
        return adbWrapper.provideDevices();
    }

    public void setInstrumentationInfo(InstrumentalExtension instrumentationInfo) {
        this.instrumentationInfo = instrumentationInfo;
    }

    public void setInstrumentationArgsProvider(InstrumentationArgsProvider argsProvider) {
        this.instrumentationArgsProvider = argsProvider;
    }

    public void setCommandsForAnnotationProvider(CommandsForAnnotationProvider commandsProvider) {
        this.commandsForAnnotationProvider = commandsProvider;
    }

    public void setCommandProvider(DeviceRunnerCommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    public void setCoverageDir(File coverageDir) {
        this.coverageDir = coverageDir;
    }

    public void setResultsDir(File resultsDir) {
        this.resultsDir = resultsDir;
    }

    public void setReportsDir(File reportsDir) {
        this.reportsDir = reportsDir;
    }

    /**
     * Sets shard device type adapter for DefaultInstrumentationArgsProvider.
     * If you use your own implementation of InstrumentationArgsProvider,
     * then write your own shard arguments generation logic.
     */
    public void setDeviceTypeAdapter(DeviceTypeAdapter deviceTypeAdapter) {
        this.deviceTypeAdapter = deviceTypeAdapter;
    }

    public File getCoverageDir() {
        if (coverageDir == null) {
            String flavor = instrumentationInfo.getFlavorName() != null ?
                    instrumentationInfo.getFlavorName() : DEFAULT_FLAVOR;
            coverageDir = new File(buildDir,
                    String.format("outputs/androidTest/coverage/%s", flavor));
            logger.d(TAG, "Coverage dir is empty, generate default value {}", coverageDir);
        }
        return coverageDir;
    }

    public File getResultsDir() {
        if (resultsDir == null) {
            String flavor = instrumentationInfo.getFlavorName() != null ?
                    instrumentationInfo.getFlavorName() : DEFAULT_FLAVOR;
            resultsDir = new File(buildDir,
                    String.format("outputs/androidTest/%s", flavor));
            logger.d(TAG, "Results dir is empty, generate default value {}", resultsDir);
        }
        return resultsDir;
    }

    public File getReportsDir() {
        if (reportsDir == null) {
            String flavor = instrumentationInfo.getFlavorName() != null ?
                    instrumentationInfo.getFlavorName() : DEFAULT_FLAVOR;
            reportsDir = new File(buildDir,
                    String.format("outputs/reports/androidTest/%s", flavor));
            logger.d(TAG, "Reports dir is empty, generate default value {}", reportsDir);
        }
        return reportsDir;
    }

    public void setRunnerLogger(RunnerLogger logger) {
        this.logger = logger;
    }

    /**
     * Sets process crashed handler.
     */
    public void setProcessCrashedHandler(ProcessCrashHandler handler) {
        this.processCrashedHandler = handler;
    }
}

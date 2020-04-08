package com.github.grishberg.tests;

import com.android.ddmlib.*;
import com.android.utils.ILogger;
import com.github.grishberg.tests.commands.CommandExecutionException;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.common.ScreenSizeParser;
import com.github.grishberg.tests.exceptions.PullCoverageException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Wraps {@link IDevice} interface.
 *
 * Adds useful extra utility methods like pullCoverageFile(), getWidth(), getHeight(), etc.
 * Adds synchronization (no parallel commands when multiple devices are connected).
 * Adds verbose logging.
 */
public class ConnectedDeviceWrapper implements IShellEnabledDevice, DeviceShellExecuter {
    private static final String TAG = ConnectedDeviceWrapper.class.getSimpleName();
    public static final String COVERAGE_FILE_NAME = "coverage.ec";
    private static final String SHELL_COMMAND_FOR_SCREEN_SIZE = "dumpsys window";
    private final IDevice device;
    private final RunnerLogger logger;
    private int deviceWidth = -1;
    private int deviceHeight = -1;

    public ConnectedDeviceWrapper(IDevice device, RunnerLogger logger) {
        this.device = device;
        this.logger = logger;
    }

    @Override
    public synchronized void executeShellCommand(String command,
                                                 IShellOutputReceiver receiver,
                                                 long maxTimeToOutputResponse,
                                                 TimeUnit maxTimeUnits) throws TimeoutException,
            AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        logger.d(TAG, "Execute shell command on {}: \"{}\"",
                device.getName(), command);
        device.executeShellCommand(command, receiver, maxTimeToOutputResponse, maxTimeUnits);
    }

    @Override
    public synchronized Future<String> getSystemProperty(String name) {
        return device.getSystemProperty(name);
    }

    @Override
    public synchronized String getName() {
        return device.getName();
    }

    /**
     * @return device density.
     */
    public synchronized int getDensity() {
        return device.getDensity();
    }

    /**
     * @return device screen width.
     */
    public synchronized int getWidth() {
        if (deviceWidth < 0) {
            calculateScreenSize();
        }
        return deviceWidth;
    }

    /**
     * @return device screen height.
     */
    public synchronized int getHeight() {
        if (deviceHeight < 0) {
            calculateScreenSize();
        }
        return deviceHeight;
    }

    /**
     * @return device screen width in dp
     */
    public synchronized long getWidthInDp() {
        if (deviceWidth < 0) {
            calculateScreenSize();
        }
        return Math.round((float) deviceWidth / (getDensity() / 160.));
    }

    /**
     * @return device screen width in dp
     */
    public synchronized long getHeightInDp() {
        if (deviceHeight < 0) {
            calculateScreenSize();
        }
        return Math.round((float) deviceHeight / (getDensity() / 160.));
    }

    private void calculateScreenSize() {
        try {
            String screenSize = executeShellCommandAndReturnOutput(SHELL_COMMAND_FOR_SCREEN_SIZE);
            int[] size = ScreenSizeParser.parseScreenSize(screenSize);
            deviceWidth = size[0];
            deviceHeight = size[1];
        } catch (CommandExecutionException e) {
            logger.e(TAG, "calculateScreenSize error: ", e);
        }
    }

    public synchronized IDevice getDevice() {
        return device;
    }

    @Override
    public synchronized String toString() {
        return "ConnectedDeviceWrapper{" +
                "sn=" + device.getSerialNumber() +
                ", isOnline=" + device.isOnline() +
                ", name='" + getName() + '\'' +
                '}';
    }

    @Override
    public synchronized void pullFile(String temporaryCoverageCopy, String path) throws CommandExecutionException {
        logger.d(TAG, "Pull file from {}: \"{}\"",
                device.getName(), path);
        try {
            device.pullFile(temporaryCoverageCopy, path);
        } catch (Throwable e) {
            throw new CommandExecutionException("pullFile exception:", e);
        }
    }

    public synchronized boolean isEmulator() {
        return device.isEmulator();
    }

    public synchronized String getSerialNumber() {
        return device.getSerialNumber();
    }

    public synchronized void installPackage(String absolutePath, boolean reinstall, String extraArgument)
            throws InstallException {
        logger.d(TAG, "Install package on {}: \"{}\"",
                device.getName(), absolutePath);
        device.installPackage(absolutePath, reinstall, extraArgument);
    }

    /**
     * Pulls coverage file from device.
     *
     * @param instrumentationInfo plugin extension with instrumentation info.
     * @param coverageFilePrefix  prefix for generating coverage on local dir.
     * @param coverageFile        full path to coverage file on target device.
     * @param outCoverageDir      local dir, where coverage file will be copied.
     * @param logger              logger.
     * @throws PullCoverageException
     */
    public synchronized void pullCoverageFile(InstrumentalExtension instrumentationInfo,
                                              String coverageFilePrefix,
                                              String coverageFile,
                                              File outCoverageDir,
                                              final ILogger logger) throws PullCoverageException {
        MultiLineReceiver outputReceiver = new MultilineLoggerReceiver(logger);

        // TODO: Why another logger is used here?
        logger.verbose("ConnectedDeviceWrapper '%s': fetching coverage data from %s",
                getName(), coverageFile);
        try {
            String temporaryCoverageCopy = "/data/local/tmp/" + instrumentationInfo.getApplicationId()
                    + "." + COVERAGE_FILE_NAME;
            executeShellCommand("run-as " + instrumentationInfo.getApplicationId() +
                            " cat " + coverageFile + " | cat > " + temporaryCoverageCopy,
                    outputReceiver,
                    2L, TimeUnit.MINUTES);
            pullFile(temporaryCoverageCopy,
                    (new File(outCoverageDir, coverageFilePrefix + "-" + COVERAGE_FILE_NAME))
                            .getPath());
            executeShellCommand("rm " + temporaryCoverageCopy, outputReceiver, 30L,
                    TimeUnit.SECONDS);
        } catch (Throwable e) {
            throw new PullCoverageException(e);
        }
    }

    @Override
    public synchronized void executeShellCommand(String command, IShellOutputReceiver receiver,
                                                 long maxTimeout, long maxTimeToOutputResponse,
                                                 TimeUnit maxTimeUnits) throws TimeoutException,
            AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        logger.d(TAG, "Execute shell command on {}: \"{}\"",
                device.getName(), command);
        device.executeShellCommand(command, receiver, maxTimeout, maxTimeToOutputResponse, maxTimeUnits);
    }

    /**
     * Execute adb shell command on device.
     *
     * @param command adb shell command for execution.
     * @throws CommandExecutionException
     */
    public synchronized void executeShellCommand(String command) throws CommandExecutionException {
        try {
            executeShellCommand(command, new CollectingOutputReceiver(), 5L, TimeUnit.MINUTES);
        } catch (Throwable e) {
            throw new CommandExecutionException("executeShellCommand exception:", e);
        }
    }

    @Override
    public synchronized String executeShellCommandAndReturnOutput(String command) throws CommandExecutionException {
        try {
            CollectingOutputReceiver receiver = new CollectingOutputReceiver();
            executeShellCommand(command, receiver, 5L, TimeUnit.MINUTES);
            return receiver.getOutput();
        } catch (Throwable e) {
            throw new CommandExecutionException("executeShellCommand exception:", e);
        }
    }

    private class MultilineLoggerReceiver extends MultiLineReceiver {
        private final ILogger logger;

        MultilineLoggerReceiver(ILogger logger) {
            this.logger = logger;
        }

        @Override
        public void processNewLines(String[] lines) {
            for (String line : lines) {
                logger.verbose(line);
            }
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ConnectedDeviceWrapper)) {
            return false;
        }
        ConnectedDeviceWrapper otherDevice = (ConnectedDeviceWrapper) obj;
        String objectSerialNumber = otherDevice.getSerialNumber();
        String serialNumber = device.getSerialNumber();
        if (objectSerialNumber != null && serialNumber != null) {
            return objectSerialNumber.equals(serialNumber);
        }
        if (serialNumber == null && objectSerialNumber == null) {
            String name = device.getName();
            String otherName = otherDevice.getName();
            if (name != null) {
                return name.equals(otherName);
            }
        }
        return false;
    }
}

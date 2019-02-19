package com.github.grishberg.tests;

import com.android.ddmlib.*;
import com.android.utils.ILogger;
import com.github.grishberg.tests.commands.ExecuteCommandException;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.common.ScreenSizeParser;
import com.github.grishberg.tests.exceptions.PullCoverageException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Wraps {@link IDevice} interface.
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
    public void executeShellCommand(String command,
                                    IShellOutputReceiver receiver,
                                    long maxTimeToOutputResponse,
                                    TimeUnit maxTimeUnits) throws TimeoutException,
            AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        device.executeShellCommand(command, receiver, maxTimeToOutputResponse, maxTimeUnits);
    }

    @Override
    public Future<String> getSystemProperty(String name) {
        return device.getSystemProperty(name);
    }

    @Override
    public String getName() {
        return device.getName();
    }

    /**
     * @return device density.
     */
    public int getDensity() {
        return device.getDensity();
    }

    private void calculateScreenSize() {
        try {
            String screenSize = executeShellCommandAndReturnOutput(SHELL_COMMAND_FOR_SCREEN_SIZE);
            int[] size = ScreenSizeParser.parseScreenSize(screenSize);
            deviceWidth = size[0];
            deviceHeight = size[1];
        } catch (ExecuteCommandException e) {
            logger.e(TAG, "calculateScreenSize error: ", e);
        }
    }

    /**
     * @return device screen width.
     */
    public int getWidth() {
        if (deviceWidth < 0) {
            calculateScreenSize();
        }
        return deviceWidth;
    }

    /**
     * @return device screen height.
     */
    public int getHeight() {
        if (deviceHeight < 0) {
            calculateScreenSize();
        }
        return deviceHeight;
    }

    /**
     * @return device screen width in dp
     */
    public long getWidthInDp() {
        if (deviceWidth < 0) {
            calculateScreenSize();
        }
        return Math.round((float) deviceWidth / (getDensity() / 160.));
    }

    /**
     * @return device screen width in dp
     */
    public long getHeightInDp() {
        if (deviceHeight < 0) {
            calculateScreenSize();
        }
        return Math.round((float) deviceHeight / (getDensity() / 160.));
    }

    public IDevice getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "ConnectedDeviceWrapper{" +
                "sn=" + device.getSerialNumber() +
                ", isOnline=" + device.isOnline() +
                ", name='" + getName() + '\'' +
                '}';
    }

    @Override
    public void pullFile(String temporaryCoverageCopy, String path) throws ExecuteCommandException {
        try {
            device.pullFile(temporaryCoverageCopy, path);
        } catch (Exception e) {
            throw new ExecuteCommandException("pullFile exception:", e);
        }
    }

    public boolean isEmulator() {
        return device.isEmulator();
    }

    public String getSerialNumber() {
        return device.getSerialNumber();
    }

    public void installPackage(String absolutePath, boolean reinstall, String extraArgument)
            throws InstallException {
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
    public void pullCoverageFile(InstrumentalExtension instrumentationInfo,
                                 String coverageFilePrefix,
                                 String coverageFile,
                                 File outCoverageDir,
                                 final ILogger logger) throws PullCoverageException {
        MultiLineReceiver outputReceiver = new MultilineLoggerReceiver(logger);

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
        } catch (Exception e) {
            throw new PullCoverageException(e);
        }
    }

    @Override
    public void executeShellCommand(String command, IShellOutputReceiver receiver,
                                    long maxTimeout, long maxTimeToOutputResponse,
                                    TimeUnit maxTimeUnits) throws TimeoutException,
            AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        device.executeShellCommand(command, receiver, maxTimeout, maxTimeToOutputResponse, maxTimeUnits);
    }

    /**
     * Execute adb shell command on device.
     *
     * @param command adb shell command for execution.
     * @throws ExecuteCommandException
     */
    public void executeShellCommand(String command) throws ExecuteCommandException {
        try {
            executeShellCommand(command, new CollectingOutputReceiver(), 5L, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new ExecuteCommandException("executeShellCommand exception:", e);
        }
    }

    @Override
    public String executeShellCommandAndReturnOutput(String command) throws ExecuteCommandException {
        try {
            CollectingOutputReceiver receiver = new CollectingOutputReceiver();
            executeShellCommand(command, receiver, 5L, TimeUnit.MINUTES);
            return receiver.getOutput();
        } catch (Exception e) {
            throw new ExecuteCommandException("executeShellCommand exception:", e);
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
    public boolean equals(Object obj) {
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

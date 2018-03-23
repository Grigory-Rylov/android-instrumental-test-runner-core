package com.github.grishberg.tests;

import com.android.ddmlib.*;
import com.android.utils.ILogger;
import com.github.grishberg.tests.exceptions.PullCoverageException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Wraps {@link IDevice} interface.
 */
public class ConnectedDeviceWrapper implements IShellEnabledDevice {
    public static final String COVERAGE_FILE_NAME = "coverage.ec";
    private final IDevice device;
    private String name;

    public ConnectedDeviceWrapper(IDevice device) {
        this.device = device;
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

    public String getName() {
        if (name == null) {
            //TODO: format device name from api level
            name = device.getAvdName();
        }
        return name;
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

    public void pullFile(String temporaryCoverageCopy, String path) throws TimeoutException,
            AdbCommandRejectedException, SyncException, IOException {
        device.pullFile(temporaryCoverageCopy, path);
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
     * @param instrumentationInfo
     * @param coverageFilePrefix  prefix for generating coverage on local dir.
     * @param coverageFile        full path to coverage file on target device.
     * @param outCoverageDir      local dir, where coverage file will be copied.
     * @param logger              logger.
     * @throws PullCoverageException
     */
    public void pullCoverageFile(InstrumentalPluginExtension instrumentationInfo,
                                 String coverageFilePrefix,
                                 String coverageFile,
                                 File outCoverageDir,
                                 final ILogger logger) throws PullCoverageException {
        MultiLineReceiver outputReceiver = new MultiLineReceiver() {
            public void processNewLines(String[] lines) {
                for (String line : lines) {
                    logger.verbose(line);
                }
            }

            public boolean isCancelled() {
                return false;
            }
        };

        logger.verbose("ConnectedDeviceWrapper '%s': fetching coverage data from %s",
                getName(), coverageFile);
        try {
            String temporaryCoverageCopy = "/data/local/tmp/" + instrumentationInfo.getApplicationId()
                    + "." + COVERAGE_FILE_NAME;
            executeShellCommand("run-as " + instrumentationInfo.getApplicationId() +
                            " cat " + coverageFile + " | cat > " + temporaryCoverageCopy,
                    outputReceiver,
                    30L, TimeUnit.SECONDS);
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
}

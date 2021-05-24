package com.github.grishberg.tests;

import com.android.ddmlib.IShellOutputReceiver;

import java.util.concurrent.TimeUnit;

/**
 * Helper interface for {@link ConnectedDeviceWrapper}.
 * Provides arguments of arbitrary adb shell command sent to device
 * with {@link ConnectedDeviceWrapper#executeShellCommand(String, IShellOutputReceiver, long, long, TimeUnit)}.
 */
public interface ShellCommand {
    /**
     * @return ADB shell command to execute on device.
     */
    String getCommand();

    /**
     * @return ADB shell command string suitable for logging (can be the same as {@link #getCommand()}.
     */
    String getLoggedCommand();

    /**
     * The maximum timeout for the command to return. A value of 0 means no max timeout will be applied.
     *
     * See more details in {@link
     * com.android.ddmlib.IShellEnabledDevice#executeShellCommand(String, IShellOutputReceiver, long, long, TimeUnit)
     * IShellEnabledDevice#executeShellCommand}
     *
     * @return command timeout specified in units specified in {@link #getMaxTimeUnits()}.
     */
    long getMaxTimeout();

    /**
     * The maximum amount of time during which the command is allowed to not output any response. A value of 0 means
     * the method will wait forever.
     *
     * See more details in {@link
     * com.android.ddmlib.IShellEnabledDevice#executeShellCommand(String, IShellOutputReceiver, long, long, TimeUnit)
     * IShellEnabledDevice#executeShellCommand}
     *
     * @return command timeout specified in units specified in {@link #getMaxTimeUnits()}.
     */
    long getMaxTimeToOutputResponse();

    /**
     * @return Units for non-zero maxTimeout and maxTimeToOutputResponse values.
     */
    TimeUnit getMaxTimeUnits();

    /**
     * The IShellOutputReceiver that will receives the output of the shell command.
     * Alternatively you may use {@link ConnectedDeviceWrapper#executeShellCommandAndReturnOutput(String)}}
     * to get all output as String.
     * In simple case use `new com.android.ddmlib.CollectingOutputReceiver()` for this.
     *
     * @return the IShellOutputReceiver instance.
     */
    IShellOutputReceiver getReceiver();
}

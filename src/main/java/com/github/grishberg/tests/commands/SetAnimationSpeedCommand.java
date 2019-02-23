package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.TestRunnerContext;

/**
 * Changes animation speed.
 */
public class SetAnimationSpeedCommand implements DeviceRunnerCommand {
    private final int windowAnimationScale;
    private final int transitionAnimationScale;
    private final int animatorDurationScale;

    public SetAnimationSpeedCommand(int windowAnimationScale, int transitionAnimationScale,
                                    int animatorDurationScale) {
        this.windowAnimationScale = windowAnimationScale;
        this.transitionAnimationScale = transitionAnimationScale;
        this.animatorDurationScale = animatorDurationScale;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device, TestRunnerContext context)
            throws CommandExecutionException {
        device.executeShellCommand(String.format("settings put global window_animation_scale %d",
                windowAnimationScale));
        device.executeShellCommand(String.format("settings put global transition_animation_scale %d",
                transitionAnimationScale));
        device.executeShellCommand(String.format("settings put global animator_duration_scale %d",
                animatorDurationScale));
        return new DeviceCommandResult();
    }
}

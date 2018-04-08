package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.TestRunnerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by grishberg on 29.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SetAnimationSpeedCommandTest {
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    TestRunnerContext context;

    @Test
    public void execute() throws Exception {
        SetAnimationSpeedCommand command = new SetAnimationSpeedCommand(0, 1, 2);

        command.execute(deviceWrapper, context);

        verify(deviceWrapper).executeShellCommand("settings put global window_animation_scale 0");
        verify(deviceWrapper).executeShellCommand("settings put global transition_animation_scale 1");
        verify(deviceWrapper).executeShellCommand("settings put global animator_duration_scale 2");
    }
}
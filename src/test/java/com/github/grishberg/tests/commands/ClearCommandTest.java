package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalExtension;
import com.github.grishberg.tests.TestRunnerContext;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 31.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClearCommandTest {
    @Mock
    RunnerLogger logger;
    @Mock
    InstrumentalExtension ext;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    TestRunnerContext context;

    @Before
    public void setUp() throws Exception {
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(deviceWrapper.getLogger()).thenReturn(logger);
        when(ext.getApplicationId()).thenReturn("appId");
    }

    @Test
    public void clearDefaultCommand() throws Exception {
        ClearCommand command = new ClearCommand();
        command.execute(deviceWrapper, context);

        Mockito.verify(deviceWrapper).executeShellCommand("pm clear appId");
        verify(logger).d(ClearCommand.class.getSimpleName(), "ClearCommand for package {}",
                "appId");
    }

    @Test
    public void clearCustomCommand() throws Exception {
        ClearCommand command = new ClearCommand("com.yandex.test");
        command.execute(deviceWrapper, context);

        Mockito.verify(deviceWrapper).executeShellCommand("pm clear com.yandex.test");
        verify(logger).d(ClearCommand.class.getSimpleName(), "ClearCommand for package {}",
                "com.yandex.test");
    }
}
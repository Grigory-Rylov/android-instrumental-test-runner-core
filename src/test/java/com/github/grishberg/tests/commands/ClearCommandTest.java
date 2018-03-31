package com.github.grishberg.tests.commands;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
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
    InstrumentalPluginExtension ext;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    private ClearCommand clearCommand;

    @Before
    public void setUp() throws Exception {
        when(ext.getApplicationId()).thenReturn("appId");
        clearCommand = new ClearCommand(logger, ext);
    }

    @Test
    public void execute() throws Exception {
        clearCommand.execute(deviceWrapper);

        Mockito.verify(deviceWrapper).executeShellCommand("pm clear appId");
        verify(logger).i(ClearCommand.class.getSimpleName(), "ClearCommand for package %s", "appId");
    }
}
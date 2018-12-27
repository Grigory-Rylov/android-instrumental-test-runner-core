package com.github.grishberg.tests;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by grishberg on 22.03.18.
 */
public class DefaultInstrumentationArgsProviderTest {
    @Test
    public void testArgsProvider() {
        ConnectedDeviceWrapper deviceWrapper = mock(ConnectedDeviceWrapper.class);
        InstrumentalPluginExtension instrumentalInfo = mock(InstrumentalPluginExtension.class);
        AbsShardingArguments sharding = mock(AbsShardingArguments.class);
        DefaultInstrumentationArgsProvider provider =
                new DefaultInstrumentationArgsProvider(instrumentalInfo, sharding);
        Assert.assertTrue(provider.provideInstrumentationArgs(deviceWrapper).isEmpty());
    }
}
package com.github.grishberg.tests;

import com.github.grishberg.tests.sharding.ShardArguments;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 22.03.18.
 */
public class DefaultInstrumentationArgsProviderTest {
    @Test
    public void dontProvideShardArgsByDefault() {
        ConnectedDeviceWrapper deviceWrapper = mock(ConnectedDeviceWrapper.class);
        InstrumentalPluginExtension instrumentalInfo = mock(InstrumentalPluginExtension.class);
        ShardArguments shardArguments = mock(ShardArguments.class);
        DefaultInstrumentationArgsProvider provider =
                new DefaultInstrumentationArgsProvider(instrumentalInfo, shardArguments);
        Assert.assertTrue(provider.provideInstrumentationArgs(deviceWrapper).isEmpty());
    }

    @Test
    public void provideShardArgsWhenEnabled() {
        ConnectedDeviceWrapper deviceWrapper = mock(ConnectedDeviceWrapper.class);
        InstrumentalPluginExtension instrumentalInfo = mock(InstrumentalPluginExtension.class);
        when(instrumentalInfo.isShardEnabled()).thenReturn(true);
        ShardArguments sharding = mock(ShardArguments.class);
        HashMap<String, String> res = new HashMap<>();
        res.put("numShard", "2");
        res.put("shardIndex", "0");
        when(sharding.createShardArguments(deviceWrapper)).thenReturn(res);
        DefaultInstrumentationArgsProvider provider =
                new DefaultInstrumentationArgsProvider(instrumentalInfo, sharding);

        Assert.assertEquals(2, provider.provideInstrumentationArgs(deviceWrapper).size());
    }
}
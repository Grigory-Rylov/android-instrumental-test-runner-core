package com.github.grishberg.tests;

import com.github.grishberg.tests.sharding.ShardArguments;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by grishberg on 20.10.17.
 */
public class DefaultInstrumentationArgsProvider implements InstrumentationArgsProvider {
    private InstrumentalPluginExtension pluginExtension;
    private final com.github.grishberg.tests.sharding.ShardArguments testShard;

    public DefaultInstrumentationArgsProvider(InstrumentalPluginExtension pluginExtension,
                                              ShardArguments testShard) {
        this.pluginExtension = pluginExtension;
        this.testShard = testShard;
    }

    @Override
    public Map<String, String> provideInstrumentationArgs(ConnectedDeviceWrapper device) {
        HashMap<String, String> arguments = new HashMap<>();
        if (pluginExtension.isShardEnabled()) {
            return testShard.createShardArguments(device);
        }
        return arguments;
    }
}

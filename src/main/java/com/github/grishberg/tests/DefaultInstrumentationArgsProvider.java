package com.github.grishberg.tests;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by grishberg on 20.10.17.
 */
public class DefaultInstrumentationArgsProvider implements InstrumentationArgsProvider {
    private InstrumentalPluginExtension pluginExtension;
    private final ShardingArguments testShard;

    public DefaultInstrumentationArgsProvider(InstrumentalPluginExtension pluginExtension,
                                              ShardingArguments testShard) {
        this.pluginExtension = pluginExtension;
        this.testShard = testShard;
    }

    @Override
    public Map<String, String> provideInstrumentationArgs(ConnectedDeviceWrapper device) {
        HashMap<String, String> arguments = new HashMap<>();
        if (pluginExtension.isShardEnabled()) {
            testShard.addShardingArguments(device, arguments);
        }
        return arguments;
    }
}

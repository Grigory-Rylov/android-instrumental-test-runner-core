package com.github.grishberg.tests;

import java.util.Map;

/**
 * Provides arguments for starting target device.
 */
public interface InstrumentationArgsProvider {
    /**
     * @param device The target device on which tests will be run.
     * @return map of arguments
     */
    Map<String, String> provideInstrumentationArgs(ConnectedDeviceWrapper device);
}

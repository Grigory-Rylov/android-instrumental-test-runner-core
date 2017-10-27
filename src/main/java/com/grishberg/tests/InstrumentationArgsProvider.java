package com.grishberg.tests;

import java.util.Map;

/**
 * Created by grishberg on 20.10.17.
 */
public interface InstrumentationArgsProvider {
    Map<String, String> provideInstrumentationArgs(DeviceWrapper targetDevice);
}

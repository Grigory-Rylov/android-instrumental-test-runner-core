package com.grishberg.tests;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by grishberg on 20.10.17.
 */
public class DefaultInstrumentationArgsProvider implements InstrumentationArgsProvider {
    @Override
    public Map<String, String> provideInstrumentationArgs(DeviceWrapper targetDevice) {
        HashMap<String, String> args = new HashMap<>();
        //args.put("annotation", "com.grishberg.gpsexample.ClearData");
        return args;
    }
}

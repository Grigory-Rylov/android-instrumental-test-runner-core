package com.github.grishberg.tests;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by grishberg on 20.10.17.
 */
public class DefaultInstrumentationArgsProvider implements InstrumentationArgsProvider {
    @Override
    public Map<String, String> provideInstrumentationArgs(DeviceWrapper targetDevice) {
        HashMap<String, String> args = new HashMap<>();
        args.put("listener", "com.github.grishberg.annotationprinter.AnnotationsTestPrinter");
        return args;
    }
}

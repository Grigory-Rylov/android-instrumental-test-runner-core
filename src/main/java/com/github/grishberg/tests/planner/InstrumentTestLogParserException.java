package com.github.grishberg.tests.planner;

/**
 * Exception while parsing output from "am instrument -e log true".
 */
public class InstrumentTestLogParserException extends Error {
    public InstrumentTestLogParserException(String message) {
        super(message);
    }
}

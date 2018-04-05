package com.github.grishberg.tests;

import groovy.lang.Closure;

import java.util.Map;

/**
 * Extension for sending arguments to plugin.
 */
public class InstrumentalPluginExtension {
    String flavorName;
    String androidSdkPath;
    String applicationId;
    String instrumentalPackage;
    String instrumentalRunner;
    String instrumentListener = "com.github.grishberg.annotationprinter.AnnotationsTestPrinter";
    boolean coverageEnabled;
    boolean makeScreenshotsWhenFail;
    Closure<Map<String, String>> instrumentationArgsProvider;

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getAndroidSdkPath() {
        return androidSdkPath;
    }

    public void setAndroidSdkPath(String androidSdkPath) {
        this.androidSdkPath = androidSdkPath;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getInstrumentalPackage() {
        return instrumentalPackage;
    }

    public void setInstrumentalPackage(String instrumentalPackage) {
        this.instrumentalPackage = instrumentalPackage;
    }

    public String getInstrumentalRunner() {
        return instrumentalRunner;
    }

    public void setInstrumentalRunner(String instrumentalRunner) {
        this.instrumentalRunner = instrumentalRunner;
    }

    public boolean isCoverageEnabled() {
        return coverageEnabled;
    }

    public void setCoverageEnabled(boolean coverageEnabled) {
        this.coverageEnabled = coverageEnabled;
    }

    public Closure<Map<String, String>> getInstrumentationArgsProvider() {
        return instrumentationArgsProvider;
    }

    public void setInstrumentationArgsProvider(Closure<Map<String, String>> instrumentationArgsProvider) {
        this.instrumentationArgsProvider = instrumentationArgsProvider;
    }

    public String getInstrumentListener() {
        return instrumentListener;
    }

    public void setInstrumentListener(String instrumentListener) {
        this.instrumentListener = instrumentListener;
    }

    public boolean isMakeScreenshotsWhenFail() {
        return makeScreenshotsWhenFail;
    }

    public void setMakeScreenshotsWhenFail(boolean makeScreenshotsWhenFail) {
        this.makeScreenshotsWhenFail = makeScreenshotsWhenFail;
    }
}

package com.github.grishberg.tests;

/**
 * Information for instrumental tests.
 */
public class InstrumentationInfo {
    private final String applicationId;
    private final String instrumentalPackage;
    private final String instrumentalRunner;
    private String flavorName = "";

    public InstrumentationInfo(String applicationId,
                               String instrumentalPackage,
                               String instrumentalRunner) {
        this.applicationId = applicationId;
        this.instrumentalPackage = instrumentalPackage;
        this.instrumentalRunner = instrumentalRunner;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getInstrumentalPackage() {
        return instrumentalPackage;
    }

    public String getInstrumentalRunner() {
        return instrumentalRunner;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public static class Builder {
        private InstrumentationInfo info;

        public Builder(String applicationId,
                       String instrumentalPackage,
                       String instrumentalRunner) {
            info = new InstrumentationInfo(applicationId,
                    instrumentalPackage,
                    instrumentalRunner);
        }

        public Builder setFlavorName(String flavorName) {
            info.flavorName = flavorName;
            return this;
        }

        public InstrumentationInfo build() {
            return info;
        }
    }
}

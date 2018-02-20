package com.github.grishberg.tests;

import java.io.File;

/**
 * Provides directories for commands provider.
 */
public class DirectoriesProvider {
    private final File resultsDir;
    private final File reportsDir;
    private final File coverageDir;

    public DirectoriesProvider(File resultsDir, File reportsDir, File coverageDir) {
        this.resultsDir = resultsDir;
        this.reportsDir = reportsDir;
        this.coverageDir = coverageDir;
    }

    public File getResultsDir() {
        return resultsDir;
    }

    public File getReportsDir() {
        return reportsDir;
    }

    public File getCoverageDir() {
        return coverageDir;
    }
}

package com.github.grishberg.tests

import com.android.ddmlib.testrunner.TestIdentifier

/**
 * Helps to extend xml report by adding more properties.
 */
interface XmlReportGeneratorDelegate {
    /**
     * Is called when created properties for report.
     * return additional custom properties there.
     */
    fun provideProperties(): Map<String, String> = HashMap()

    /**
     * Is called when generates attributes for test tag.
     * return additional custom attributes for test tag.
     */
    fun provideAdditionalAttributesForTest(testId: TestIdentifier): Map<String, String> = HashMap()

    object STUB : XmlReportGeneratorDelegate
}
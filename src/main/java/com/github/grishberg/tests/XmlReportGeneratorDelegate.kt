package com.github.grishberg.tests

/**
 * Helps to extend xml report by adding more properties.
 */
interface XmlReportGeneratorDelegate {
    /**
     * Is called when created properties for report.
     * return additional custom properties there.
     */
    fun provideProperties(): Map<String, String> = HashMap()

    object STUB : XmlReportGeneratorDelegate
}
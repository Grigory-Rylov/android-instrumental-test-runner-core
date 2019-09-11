package com.github.grishberg.tests.common

import com.android.ddmlib.testrunner.ITestRunListener
import com.android.ddmlib.testrunner.TestIdentifier

/**
 * ITestRunListener empty implementation.
 */
abstract class EmptyTestRunListener : ITestRunListener {
    override fun testRunStarted(runName: String?, testCount: Int) = Unit

    override fun testStarted(test: TestIdentifier?) = Unit

    override fun testAssumptionFailure(test: TestIdentifier?, trace: String?) = Unit

    override fun testRunStopped(elapsedTime: Long) = Unit

    override fun testFailed(test: TestIdentifier?, trace: String?) = Unit

    override fun testEnded(test: TestIdentifier?, testMetrics: MutableMap<String, String>?) = Unit

    override fun testIgnored(test: TestIdentifier?) = Unit

    override fun testRunFailed(errorMessage: String?) = Unit

    override fun testRunEnded(elapsedTime: Long, runMetrics: MutableMap<String, String>?) = Unit
}
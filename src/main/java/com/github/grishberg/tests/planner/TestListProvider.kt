package com.github.grishberg.tests.planner

/**
 * Provides list of tests.
 */
interface TestListProvider {
    fun provideTestList(): List<TestPlanElement>
}

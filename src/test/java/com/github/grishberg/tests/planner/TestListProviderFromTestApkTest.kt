package com.github.grishberg.tests.planner

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class TestListProviderFromTestApkTest {
    private val underTest: TestListProviderFromTestApk

    init {
        val classLoader = javaClass.classLoader
        val testApk = File(classLoader.getResource("test.apk").file)
        underTest = TestListProviderFromTestApk(testApk)
    }

    @Test
    fun `check parsed test methods count`() {
        val plan = underTest.provideTestList()
        assertEquals(4, plan.size)
    }

    @Test
    fun `check first test class name`() {
        val plan = underTest.provideTestList()

        assertEquals(
            "com.github.grishberg.instrumentaltestsample.MainActivityTest",
            plan.first().className
        )
    }

    @Test
    fun `check first test method name`() {
        val plan = underTest.provideTestList()

        assertEquals("testPhoneButton", plan.first().methodName)
    }

    @Test
    fun `check first test annotations`() {
        val plan = underTest.provideTestList()

        val first = plan.first()

        assertEquals(3, first.annotations.size)

        assertEquals("org.junit.runner.RunWith", first.annotations[0].name)
        assertEquals(
            "com.github.grishberg.instrumentaltestsample.CustomAnnotationWithParameters",
            first.annotations[1].name
        )
        assertEquals("org.junit.Test", first.annotations[2].name)
    }

    @Test
    fun `check first test custom annotation parameters name`() {
        val plan = underTest.provideTestList()

        val customAnnotation = plan.first().annotations[1]

        assertEquals(
            6,
            customAnnotation.members.size
        )

        assertEquals(
            "doubleValue",
            customAnnotation.members[0].name
        )

        assertEquals(
            "intArrayValue",
            customAnnotation.members[1].name
        )

        assertEquals(
            "intValue",
            customAnnotation.members[2].name
        )

        assertEquals(
            "longValue",
            customAnnotation.members[3].name
        )

        assertEquals(
            "stringArrayValue",
            customAnnotation.members[4].name
        )

        assertEquals(
            "stringValue",
            customAnnotation.members[5].name
        )
    }

    @Test
    fun `check first test custom annotation parameters values`() {
        val plan = underTest.provideTestList()

        val customAnnotation = plan.first().annotations[1]

        assertEquals(
            100.0,
            customAnnotation.members[0].doubleValue
        )

        assertEquals(
            listOf(0, 1, 2),
            customAnnotation.members[1].intArray
        )

        assertEquals(
            125,
            customAnnotation.members[2].intValue
        )

        assertEquals(
            10L,
            customAnnotation.members[3].longValue
        )

        assertEquals(
            listOf("1", "2", "3"),
            customAnnotation.members[4].strArray
        )

        assertEquals(
            "test string",
            customAnnotation.members[5].strValue
        )
    }
}

package com.github.grishberg.tests.planner

import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.DexParser
import java.io.File

/**
 * Provides list of tests by parsing test-apk.
 */
class TestListProviderFromTestApk(
    private val testApk: File
) : TestListProvider {

    override fun provideTestList(): List<TestPlanElement> {
        val testMethods = DexParser.findTestMethods(testApk.absolutePath)

        val res = mutableListOf<TestPlanElement>()
        for (testMethod in testMethods) {
            val testMethodDividerPos = testMethod.testName.lastIndexOf('#')
            if (testMethodDividerPos < 0) {
                throw IllegalArgumentException("Has no test method divider '#'")
            }
            val className = testMethod.testName.substring(0, testMethodDividerPos)
            val testName = testMethod.testName.substring(testMethodDividerPos + 1)
            val annotations = mutableListOf<AnnotationInfo>()
            for (annotation in testMethod.annotations) {
                val annotationParameters = mutableListOf<AnnotationMember>()
                for (annotationValue in annotation.values) {
                    val parameterName = annotationValue.key
                    val parameter = when (val decodedValue = annotationValue.value) {
                        is DecodedValue.DecodedString -> AnnotationMember(
                            name = parameterName,
                            valueType = "string",
                            strValue = decodedValue.value
                        )
                        is DecodedValue.DecodedInt -> AnnotationMember(
                            name = parameterName,
                            valueType = "int",
                            intValue = decodedValue.value
                        )
                        is DecodedValue.DecodedLong -> AnnotationMember(
                            name = parameterName,
                            valueType = "long",
                            longValue = decodedValue.value
                        )
                        is DecodedValue.DecodedBoolean -> AnnotationMember(
                            name = parameterName,
                            valueType = "boolean",
                            boolValue = decodedValue.value
                        )
                        is DecodedValue.DecodedType -> AnnotationMember(
                            name = parameterName,
                            valueType = "type",
                            strValue = decodedValue.value
                        )
                        is DecodedValue.DecodedDouble -> AnnotationMember(
                            name = parameterName,
                            valueType = "double",
                            doubleValue = decodedValue.value
                        )
                        is DecodedValue.DecodedArrayValue -> {
                            createArrayAnnotationMember(parameterName, decodedValue.values)
                        }
                        else -> throw IllegalStateException("Unknown parameter type $decodedValue")
                    }
                    annotationParameters.add(parameter)
                }
                annotations.add(AnnotationInfo(annotation.name, annotationParameters))
            }
            res.add(TestPlanElement("", testName, className, annotations))
        }
        return res
    }

    private fun createArrayAnnotationMember(
        parameterName: String,
        values: Array<DecodedValue>
    ): AnnotationMember {
        val stringValues = mutableListOf<String>()
        val intValues = mutableListOf<Int>()

        for (arrayValue in values) {
            when (arrayValue) {
                is DecodedValue.DecodedString -> {
                    stringValues.add(arrayValue.value)
                }

                is DecodedValue.DecodedInt -> {
                    intValues.add(arrayValue.value)
                }
                else -> throw IllegalStateException("Unknown array type $values")
            }
        }
        if (stringValues.isNotEmpty()) return AnnotationMember(
            name = parameterName,
            valueType = "string[]",
            strArray = stringValues
        )
        if (intValues.isNotEmpty()) return AnnotationMember(
            name = parameterName,
            valueType = "int[]",
            intArray = intValues
        )
        throw IllegalStateException("Unknown array type $values")
    }
}

package com.github.grishberg.tests.planner

import java.util.*
import kotlin.collections.HashMap

/**
 * Annotation members (arguments).
 */
data class AnnotationMember(
        /**
         * Argument name.
         */
        val name: String,
        /**
         * Type of stored value.
         */
        val valueType: String,
        /**
         * Not null, when argument's type is integer.
         */
        var intValue: Int? = null,
        /**
         * Not null, when argument's type is String.
         */
        var strValue: String? = null,
        var strArray: ArrayList<String>? = null,
        var intArray: ArrayList<Int>? = null,
        var boolValue: Boolean? = null
)

/**
 * Abstraction of annotation.
 */
data class AnnotationInfo(
        /**
         * Annotation full name.
         */
        val name: String,
        /**
         * Annotation values(members).
         */
        val members: List<AnnotationMember>
) {
    private var membersMap: HashMap<String, AnnotationMember>? = null

    constructor(name: String) : this(name, Collections.emptyList())

    fun getMembersMap(): Map<String, AnnotationMember> {
        if (membersMap == null) {
            membersMap = HashMap()
            for (m in members) {
                membersMap!![m.name] = m
            }
        }
        return membersMap!!
    }
}
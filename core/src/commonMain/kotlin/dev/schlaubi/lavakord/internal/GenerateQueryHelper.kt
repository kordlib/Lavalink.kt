package dev.schlaubi.lavakord.internal

import dev.kord.codegen.ksp.annotations.ProcessorAnnotation
import dev.schlaubi.lavakord.PluginApi

/**
 * Annotation to generate utility functions for search queries.
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@ProcessorAnnotation("dev.schlaubi.lavakord.internal.processing")
@PluginApi
public annotation class GenerateQueryHelper(
    val serviceName: String,
    val serviceWebsite: String,
    val generateSearchAndPlayFunction: Boolean,
    val packageName: String,
    val prefix: String,
    val parameters: Array<Parameter> = [Parameter("query")],
    val builderOptions: Array<Parameter> = [],
    val builderFunction: String = "",
    val operationName: String = "search"
) {
    @Target(AnnotationTarget.FILE)
    @Retention(AnnotationRetention.SOURCE)
    @ProcessorAnnotation("dev.schlaubi.lavakord.internal.processing")
    public annotation class Parameter(
        val name: String,
        val queryName: String = "",
        val kDoc: String = "",
        val type: Type = Type.STRING,
        val enumTypes: Array<EnumType> = []
    ) {

        @ProcessorAnnotation("dev.schlaubi.lavakord.internal.processing")
        public annotation class EnumType(
            val name: String,
            val value: String,
            val kDoc: String
        )

        public enum class Type {
            STRING,
            INT,
            DOUBLE,
            ENUM
        }
    }
}

/**
 * Interface for all generated query builders.
 */
public interface QueryBuilder

internal fun taggedQuery(vararg parameters: Pair<String, Any?>): String = buildString {
    val iterator = parameters.iterator()
    while (iterator.hasNext()) {
        val (name, value) = iterator.next()
        if (name.isEmpty()) {
            append(value)
        } else if (value != null) {
            append("$name:${value.toString().replace(" ", "%20")}")
        }
        if (iterator.hasNext() && value != null) {
            append("%20")
        }
    }
}

internal fun query(vararg parameters: Pair<String, Any?>) = buildString {
    val iterator = parameters.iterator()
    while (iterator.hasNext()) {
        val (name, value) = iterator.next()
        if (name.isEmpty()) {
            append(value)
        } else if (value != null) {
            append("$name:${value.toString().replace(" ", "%20")}")
        }
        if (iterator.hasNext() && value != null) {
            append('&')
        }
    }
}

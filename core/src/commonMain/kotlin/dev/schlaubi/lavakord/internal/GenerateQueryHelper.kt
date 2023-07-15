package dev.schlaubi.lavakord.internal

/**
 * Annotation to generate utility functions for search queries.
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
internal annotation class GenerateQueryHelper(
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
    annotation class Parameter(
        val name: String,
        val queryName: String = "",
        val kDoc: String = "",
        val type: Type = Type.STRING,
        val enumTypes: Array<EnumType> = []
    ) {
        annotation class EnumType(
            val name: String,
            val value: String,
            val kDoc: String
        )

        enum class Type {
            STRING,
            INT,
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

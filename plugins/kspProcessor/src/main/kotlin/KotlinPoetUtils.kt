@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package dev.schlaubi.lavakord.ksp

import com.squareup.kotlinpoet.*
import dev.schlaubi.lavakord.PluginApi
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod


internal fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

@PluginApi
internal fun GenerateQueryHelper.Parameter.Type.toType() = when (this) {
    GenerateQueryHelper.Parameter.Type.STRING -> STRING
    GenerateQueryHelper.Parameter.Type.INT -> INT
    GenerateQueryHelper.Parameter.Type.DOUBLE -> DOUBLE
    else -> error("Unsupported type: $this")
}

internal fun TypeName.asNullable() = copy(nullable = true)

internal fun KFunction<*>.asMemberName(): MemberName {
    val packageName = javaMethod?.declaringClass?.packageName ?: error("Missing function")
    return MemberName(packageName, name)
}

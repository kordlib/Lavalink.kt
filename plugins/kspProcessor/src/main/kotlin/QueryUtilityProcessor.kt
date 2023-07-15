@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package dev.schlaubi.lavakord.ksp

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.isDefault
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.*
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import dev.schlaubi.lavakord.ksp.AnnotationArguments.Companion.arguments
import kotlin.reflect.KProperty1

private inline fun <reified A : Annotation> KSAnnotation.isOfType() = isOfType(A::class.qualifiedName!!)

internal fun KSAnnotation.isOfType(qualifiedName: String) =
    annotationType.resolve().declaration.let { if (it is KSTypeAlias) it.findActualType() else it }.qualifiedName?.asString() == qualifiedName

/**
 * Provider for our symbol processor.
 */
class QueryUtilityProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = QueryUtilityProcessor(environment)
}

private class QueryUtilityProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(GenerateQueryHelper::class.qualifiedName!!).filterIsInstance<KSFile>()
            .flatMap { it.annotations.filter { annotation -> annotation.isOfType<GenerateQueryHelper>() } }.map {
                it.toGenerateQueryHelper() to it.parent
            }.groupBy { (annotation) -> annotation.serviceName }.forEach { (serviceName, items) ->
                val (firstItem, file) = items.first()
                val children = items.map { (annotation) ->
                    annotation
                }
                generateHelpers(
                    serviceName.replace("\\s+".toRegex(), ""),
                    firstItem.packageName,
                    children,
                    environment,
                    file as KSFile
                )
            }

        return emptyList()
    }
}

private fun KSAnnotation.toGenerateQueryHelper(): GenerateQueryHelper {
    val arguments = arguments<GenerateQueryHelper>()

    val parameters = arguments[GenerateQueryHelper::parameters]?.map { it.toParameter() }
        ?: listOf(GenerateQueryHelper.Parameter("query"))
    val builderOptions = (arguments[GenerateQueryHelper::builderOptions] ?: emptyList()).map { it.toParameter() }

    return GenerateQueryHelper(
        serviceName = arguments[GenerateQueryHelper::serviceName]!!,
        serviceWebsite = arguments[GenerateQueryHelper::serviceWebsite]!!,
        generateSearchAndPlayFunction = arguments[GenerateQueryHelper::generateSearchAndPlayFunction]!!,
        packageName = arguments[GenerateQueryHelper::packageName]!!,
        prefix = arguments[GenerateQueryHelper::prefix]!!,
        parameters = parameters.toTypedArray(),
        builderOptions = builderOptions.toTypedArray(),
        builderFunction = arguments[GenerateQueryHelper::builderFunction] ?: "",
        operationName = arguments[GenerateQueryHelper::operationName] ?: "search"
    )
}

@Suppress("ANNOTATION_ARGUMENT_MUST_BE_CONST", "ANNOTATION_ARGUMENT_MUST_BE_ENUM_CONST")
private fun KSAnnotation.toParameter(): GenerateQueryHelper.Parameter {
    val arguments = arguments<GenerateQueryHelper.Parameter>()

    return GenerateQueryHelper.Parameter(
        name = arguments[GenerateQueryHelper.Parameter::name]!!,
        queryName = arguments[GenerateQueryHelper.Parameter::queryName] ?: "",
        kDoc = arguments[GenerateQueryHelper.Parameter::kDoc] ?: "",
        type = arguments[GenerateQueryHelper.Parameter::type] ?: GenerateQueryHelper.Parameter.Type.STRING,
        enumTypes = (arguments[GenerateQueryHelper.Parameter::enumTypes] ?: emptyList()).map {
            it.toEnumType()
        }.toTypedArray()
    )
}

@Suppress("ANNOTATION_ARGUMENT_MUST_BE_CONST", "ANNOTATION_ARGUMENT_MUST_BE_ENUM_CONST")
private fun KSAnnotation.toEnumType(): GenerateQueryHelper.Parameter.EnumType {
    val arguments = arguments<GenerateQueryHelper.Parameter.EnumType>()

    return GenerateQueryHelper.Parameter.EnumType(
        name = arguments[GenerateQueryHelper.Parameter.EnumType::name]!!,
        value = arguments[GenerateQueryHelper.Parameter.EnumType::value]!!,
        kDoc = arguments[GenerateQueryHelper.Parameter.EnumType::kDoc]!!
    )
}

internal class AnnotationArguments<A : Annotation> private constructor(
    private val arguments: Map<String, KSValueArgument>,
) {
    private fun getArgument(parameter: KProperty1<A, Any>) = arguments.getValue(parameter.name)
    private val KProperty1<A, Any>.value get() = getArgument(this).value

    fun isDefault(parameter: KProperty1<A, Any>) = getArgument(parameter).isDefault()

    // can't return non-nullable values because of https://github.com/google/ksp/issues/885
    operator fun get(parameter: KProperty1<A, Int>) = parameter.value as Int?
    operator fun get(parameter: KProperty1<A, Boolean>) = parameter.value as Boolean?
    operator fun get(parameter: KProperty1<A, String>) = parameter.value as String?
    operator fun get(parameter: KProperty1<A, Annotation>) = parameter.value as KSAnnotation?
    inline operator fun <reified E : Enum<E>> get(parameter: KProperty1<A, E>) =
        (parameter.value as KSType?)?.toEnumEntry<E>()

    @JvmName("getStrings")
    operator fun get(parameter: KProperty1<A, Array<out String>>) =
        @Suppress("UNCHECKED_CAST") (parameter.value as List<String>?)

    @JvmName("getAnnotations")
    operator fun get(parameter: KProperty1<A, Array<out Annotation>>) =
        @Suppress("UNCHECKED_CAST") (parameter.value as List<KSAnnotation>?)

    companion object {
        fun <A : Annotation> KSAnnotation.arguments() =
            AnnotationArguments<A>(arguments.associateBy { it.name!!.asString() })
    }
}

/** Maps [KSType] to an entry of the enum class [E]. */
private inline fun <reified E : Enum<E>> KSType.toEnumEntry(): E {
    val decl = declaration
    require(decl is KSClassDeclaration && decl.classKind == ClassKind.ENUM_ENTRY)
    val name = decl.qualifiedName!!
    require(name.getQualifier() == E::class.qualifiedName)
    return enumValueOf(name.getShortName())
}

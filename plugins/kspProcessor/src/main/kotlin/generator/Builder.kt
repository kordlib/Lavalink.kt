@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(DelicateKotlinPoetApi::class)

package dev.schlaubi.lavakord.ksp.generator

import com.squareup.kotlinpoet.*
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import dev.schlaubi.lavakord.ksp.*


@Suppress("EQUALS_MISSING")
internal fun GenerateQueryHelper.generateBuilder(name: ClassName) = TypeSpec.classBuilder(name).apply {
    addAnnotation(AnnotationSpec.get(Suppress("MemberVisibilityCanBePrivate")))
    addSuperinterface(QUERY_BUILDER)
    builderOptions.forEach {
        val type = if (it.type == GenerateQueryHelper.Parameter.Type.ENUM) {
            val enumName = name.nestedClass(it.name.capitalize())
            val enumSpec = generateEnum(enumName, it)

            addType(enumSpec)
            enumName
        } else {
            it.type.toType()
        }
        val spec = PropertySpec.builder(it.name, type.asNullable()).apply {
            mutable(true)
            addKdoc(it.kDoc)
            initializer("null")
        }.build()

        addProperty(spec)
    }
    val buildFunction = FunSpec.builder("toQuery").apply {
        this@generateBuilder.parameters.forEach { parameter ->
            addParameter(parameter.name, parameter.type.toType())
        }
        addModifiers(KModifier.INTERNAL)
        addAnnotation(AnnotationSpec.get(PublishedApi()))
        returns(STRING)

        val allParameters = this@generateBuilder.parameters + this@generateBuilder.builderOptions
        val parameters = allParameters
            .map {
                CodeBlock.of("%S·to·%L", it.queryName, it.name)
            }.joinToCode(", ")
        val packageName = builderFunction.substringBeforeLast('.')
        val functionName = builderFunction.substringAfterLast('.')
        val function = MemberName(packageName, functionName)
        addAnnotation(AnnotationSpec.get(Suppress("INVISIBLE_MEMBER")))
        addStatement("return·%M(%L)", function, parameters)
    }.build()
    addFunction(buildFunction)
    addKdoc("Builder for $serviceName $operationName queries.")
    val companion = TypeSpec.companionObjectBuilder()
        .apply {
            val property = PropertySpec.builder("Default", name)
                .initializer("%T()", name)
                .addKdoc("An instance of the builder with default values")
                .build()
            addProperty(property)
        }
        .build()
    addType(companion)
}.build()

/**
 * Generated an enum like this:
 *
 * ```kotlin
 * /**
 *   * Type of [{parameterName}].
 *   */
 *  public enum class {parameterName}(
 *      /**
 *       * The value used in queries
 *       */
 *      public val `value`: String,
 *  ) {
 *      /**
 *       * {kDoc}.
 *       */
 *      {name}("{value}"),
 *      ;
 *
 *      override fun toString(): String = value
 *  }
 *```
 */
private fun generateEnum(className: ClassName, parameter: GenerateQueryHelper.Parameter): TypeSpec {
    val enumSpec = TypeSpec.enumBuilder(className).apply {
        val constructor = FunSpec.constructorBuilder().addParameter("value", STRING).build()
        primaryConstructor(constructor)
        addProperty(
            PropertySpec.builder("value", STRING)
                .addKdoc("The value used in queries")
                .initializer("value")
                .build()
        )
        parameter.enumTypes.forEach { constant ->
            val constructorCall =
                TypeSpec.anonymousClassBuilder()
                    .addSuperclassConstructorParameter("%S", constant.value)
                    .addKdoc(constant.kDoc)
                    .build()
            addEnumConstant(constant.name, constructorCall)
        }

        val toString = FunSpec.builder("toString")
            .returns(STRING)
            .addModifiers(KModifier.OVERRIDE)
            .addCode("""return·value""")
            .build()
        addFunction(toString)
        addKdoc("Type of [%T].", className)
    }.build()
    return enumSpec
}

/**
 * Generates a function like this.
 *
 * ```kotlin
 * /**
 *  * Creates a new [{builderName}] and applies [builder] to it
 *  */
 * public inline fun {serviceName}{operationName}Query(builder: {serviceName}.() -> Unit): {serviceName} {
 *     contract { callsInPlace(builder, EXACTLY_ONCE) }
 *     return {serviceName}().apply(builder)
 * }
 * ```
 */
internal fun generateBuilderFunction(builderName: ClassName): FunSpec {
    val functionName = builderName.simpleName.replaceFirstChar { it.lowercase() }.substringBefore("Builder")
    val applyFunction = LambdaTypeName.get(receiver = builderName, returnType = UNIT)

    val parameter = ParameterSpec.builder("builder", applyFunction).build()

    return FunSpec.builder(functionName)
        .addModifiers(KModifier.INLINE)
        .addParameter(parameter).returns(builderName)
        .addBuilderContract(parameter)
        .addStatement("""return %T().apply(%N)""", builderName, parameter)
        .addKdoc("Creates a new [%T] and applies [%N] to it", builderName, parameter)
        .build()
}

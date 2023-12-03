@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(DelicateKotlinPoetApi::class)

package dev.schlaubi.lavakord.ksp.generator

import com.squareup.kotlinpoet.*
import dev.kord.codegen.kotlinpoet.*
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import dev.schlaubi.lavakord.ksp.*

@OptIn(DelicateKotlinPoetApi::class)
@Suppress("EQUALS_MISSING")
internal fun GenerateQueryHelper.generateBuilder(name: ClassName) = TypeSpec.`class`(name) {
    addAnnotation(Suppress("MemberVisibilityCanBePrivate"))
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
        addProperty(it.name, type.asNullable()) {
            addKdoc(it.kDoc)
            mutable(true)
            initializer("null")
        }
    }

    addFunction("toQuery") {
        this@generateBuilder.parameters.forEach { parameter ->
            addParameter(parameter.name, parameter.type.toType())
        }
        addModifiers(KModifier.INTERNAL)
        addAnnotation(PublishedApi())
        returns<String>()

        val allParameters = this@generateBuilder.parameters + this@generateBuilder.builderOptions
        val parameters = allParameters
            .map {
                CodeBlock.of("%S·to·%L", it.queryName, it.name)
            }.joinToCode(", ")
        val packageName = builderFunction.substringBeforeLast('.')
        val functionName = builderFunction.substringAfterLast('.')
        val function = MemberName(packageName, functionName)
        addAnnotation(Suppress("INVISIBLE_MEMBER"))
        addStatement("return·%M(%L)", function, parameters)
    }
    addKdoc("Builder for $serviceName $operationName queries.")
    addCompanionObject {
        addProperty("Default", name) {
            initializer("%T()", name)
            addKdoc("An instance of the builder with default values")
        }
    }
}

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
private fun generateEnum(className: ClassName, parameter: GenerateQueryHelper.Parameter): TypeSpec =
    TypeSpec.enum(className) {
        val constructor = FunSpec.constructorBuilder().addParameter("value", STRING).build()
        primaryConstructor(constructor)
        addProperty<String>("value") {
            addKdoc("The value used in queries")
            initializer("value")
        }
        parameter.enumTypes.forEach { constant ->
            addEnumConstant(constant.name) {
                addSuperclassConstructorParameter("%S", constant.value)
                addKdoc(constant.kDoc)
            }
        }

        addFunction("toString") {
            returns<String>()
            addModifiers(KModifier.OVERRIDE)
            addCode("""return·value""")
        }
        addKdoc("Type of [%T].", className)
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

    val parameter = ParameterSpec("builder", applyFunction)

    return FunSpec(functionName) {
        addModifiers(KModifier.INLINE)
        addParameter(parameter)
        returns(builderName)
        addBuilderContract(parameter)
        addStatement("""return %T().apply(%N)""", builderName, parameter)
        addKdoc("Creates a new [%T] and applies [%N] to it", builderName, parameter)
    }
}

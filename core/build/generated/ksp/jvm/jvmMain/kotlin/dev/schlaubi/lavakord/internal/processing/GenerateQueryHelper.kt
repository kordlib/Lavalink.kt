@file:Suppress(names = arrayOf("DataClassPrivateConstructor"))

package dev.schlaubi.lavakord.`internal`.processing

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import dev.kord.codegen.ksp.annotations.AnnotationArguments.Companion.arguments
import dev.kord.codegen.ksp.annotations.AnnotationArguments.NonNullAnnotationArguments.Companion.notNull
import dev.kord.codegen.ksp.getAnnotationByType
import dev.kord.codegen.ksp.getAnnotationsByType
import dev.schlaubi.lavakord.`internal`.GenerateQueryHelper as Annotation

/**
 * Data class representation of [Annotation].
 * @see Companion.GenerateQueryHelper
 */
public data class GenerateQueryHelper private constructor(
  public val serviceName: String,
  public val serviceWebsite: String,
  public val generateSearchAndPlayFunction: Boolean,
  public val packageName: String,
  public val prefix: String,
  public val parameters: List<Parameter>,
  public val builderOptions: List<Parameter>,
  public val builderFunction: String,
  public val operationName: String,
) {
  /**
   * Data class representation of [Annotation.Parameter].
   * @see Companion.Parameter
   */
  public data class Parameter private constructor(
    public val name: String,
    public val queryName: String,
    public val kDoc: String,
    public val type: Annotation.Parameter.Type,
    public val enumTypes: List<EnumType>,
  ) {
    /**
     * Data class representation of [Annotation.Parameter.EnumType].
     * @see Companion.EnumType
     */
    public data class EnumType private constructor(
      public val name: String,
      public val `value`: String,
      public val kDoc: String,
    ) {
      public companion object {
        /**
         * Creates an [EnumType] from an [KSAnnotation].
         */
        public fun EnumType(`annotation`: KSAnnotation): EnumType {
          val arguments = `annotation`.arguments<Annotation.Parameter.EnumType>().notNull()

          val name = arguments[Annotation.Parameter.EnumType::name]
          val value = arguments[Annotation.Parameter.EnumType::value]
          val kDoc = arguments[Annotation.Parameter.EnumType::kDoc]
          return EnumType(name, value, kDoc)
        }
      }
    }

    public companion object {
      /**
       * Creates an [Parameter] from an [KSAnnotation].
       */
      public fun Parameter(`annotation`: KSAnnotation): Parameter {
        val arguments = `annotation`.arguments<Annotation.Parameter>().notNull()

        val name = arguments[Annotation.Parameter::name]
        val queryName = arguments[Annotation.Parameter::queryName]
        val kDoc = arguments[Annotation.Parameter::kDoc]
        val type = arguments[Annotation.Parameter::type]
        val enumTypes = arguments[Annotation.Parameter::enumTypes].map(EnumType.Companion::EnumType)
        return Parameter(name, queryName, kDoc, type, enumTypes)
      }
    }
  }

  public companion object {
    /**
     * Creates an [GenerateQueryHelper] from an [KSAnnotation].
     */
    public fun GenerateQueryHelper(`annotation`: KSAnnotation): GenerateQueryHelper {
      val arguments = `annotation`.arguments<Annotation>().notNull()

      val serviceName = arguments[Annotation::serviceName]
      val serviceWebsite = arguments[Annotation::serviceWebsite]
      val generateSearchAndPlayFunction = arguments[Annotation::generateSearchAndPlayFunction]
      val packageName = arguments[Annotation::packageName]
      val prefix = arguments[Annotation::prefix]
      val parameters = arguments[Annotation::parameters].map(Parameter.Companion::Parameter)
      val builderOptions = arguments[Annotation::builderOptions].map(Parameter.Companion::Parameter)
      val builderFunction = arguments[Annotation::builderFunction]
      val operationName = arguments[Annotation::operationName]
      return GenerateQueryHelper(serviceName, serviceWebsite, generateSearchAndPlayFunction, packageName, prefix, parameters, builderOptions, builderFunction, operationName)
    }
  }
}

/**
 * Returns a [Sequence] of all [GenerateQueryHelper] annotations on this [element](KSAnnotated)
 */
public fun KSAnnotated.getGenerateQueryHelpers(): Sequence<GenerateQueryHelper> = getAnnotationsByType<Annotation>().map(GenerateQueryHelper.Companion::GenerateQueryHelper)

/**
 * Returns a GenerateQueryHelper annotation [element](KSAnnotated)
 */
public fun KSAnnotated.getParameter(): GenerateQueryHelper = getAnnotationByType<Annotation.Parameter>().let(GenerateQueryHelper.Companion::GenerateQueryHelper)

/**
 * Returns a GenerateQueryHelper annotation [element](KSAnnotated)
 */
public fun KSAnnotated.getEnumType(): GenerateQueryHelper = getAnnotationByType<Annotation.Parameter.EnumType>().let(GenerateQueryHelper.Companion::GenerateQueryHelper)

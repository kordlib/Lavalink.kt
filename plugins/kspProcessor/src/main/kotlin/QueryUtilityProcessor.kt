@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package dev.schlaubi.lavakord.ksp

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSTypeAlias
import dev.schlaubi.lavakord.PluginApi
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import dev.schlaubi.lavakord.internal.processing.getGenerateQueryHelpers

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
    @OptIn(PluginApi::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        environment.logger.warn("Got new files, starting process!!")
        resolver.getSymbolsWithAnnotation(GenerateQueryHelper::class.qualifiedName!!)
            .onEach { environment.logger.warn("Processing element", it) }
            .filterIsInstance<KSFile>()
            .map { it to it.getGenerateQueryHelpers() }
            .forEach { (serviceName, items) ->
                environment.logger.warn("Processing item", serviceName)
                generateHelpers(
                    serviceName.fileName.replace("\\s+".toRegex(), ""),
                    serviceName.packageName.asString(),
                    items.toList(),
                    environment,
                    serviceName
                )
            }

        return emptyList()
    }
}

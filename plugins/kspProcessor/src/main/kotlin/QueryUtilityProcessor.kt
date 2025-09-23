@file:Suppress("INVISIBLE_MEMBER")

package dev.schlaubi.lavakord.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import dev.schlaubi.lavakord.PluginApi
import dev.schlaubi.lavakord.internal.GenerateQueryHelper

/**
 * Provider for our symbol processor.
 */
class QueryUtilityProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = QueryUtilityProcessor(environment)
}

private class QueryUtilityProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    @OptIn(PluginApi::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(GenerateQueryHelper::class.qualifiedName!!)
            .filterIsInstance<KSFile>()
            .map { it to it.getGenerates() }
            .forEach { (serviceName, items) ->
                generateHelpers(
                    serviceName.fileName.replace(".kt", "").replace("\\s+".toRegex(), ""),
                    serviceName.packageName.asString(),
                    items.toList(),
                    environment,
                    serviceName
                )
            }

        return emptyList()
    }
}

@OptIn(KspExperimental::class)
private fun KSAnnotated.getGenerates() = getAnnotationsByType(GenerateQueryHelper::class)

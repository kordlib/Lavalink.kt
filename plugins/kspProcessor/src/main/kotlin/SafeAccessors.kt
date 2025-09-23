package dev.schlaubi.lavakord.ksp

import dev.schlaubi.lavakord.internal.GenerateQueryHelper


val GenerateQueryHelper.operationNameSafe: String
    get() = runCatching { operationName }.getOrDefault("search")
val GenerateQueryHelper.Parameter.queryNameSafe: String
    get() = runCatching { queryName }.getOrDefault("")
val GenerateQueryHelper.Parameter.typeSafe: GenerateQueryHelper.Parameter.Type
    get() = runCatching { type }.getOrDefault(GenerateQueryHelper.Parameter.Type.STRING)
val GenerateQueryHelper.builderOptionsSafe: Array<GenerateQueryHelper.Parameter>
    get() = runCatching { builderOptions }.getOrDefault(emptyArray())
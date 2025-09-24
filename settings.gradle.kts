@file:Suppress("KDocMissingDocumentation")

pluginManagement {
    resolutionStrategy {
        repositories {
            gradlePluginPortal()
            maven("https://europe-west3-maven.pkg.dev/mik-music/kord")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "lavakord"
include(
    "example",
    "kord", // GitHub Actions gets mad about this and I can't reproduce this locally
//    "jsExample",
    "core",
    ":plugins:kspProcessor",
    ":plugins:sponsorblock",
    ":plugins:lavasrc",
    ":plugins:lavasearch",
    ":plugins:lyrics",
    "java",
    "jda",
    "jda-java"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val codegen = version("codegen", "1.0.0")
            val kord = version("kord", "0.17.0")
            kotlinx()
            ktor()
            ksp()
            library("kord-core", "dev.kord", "kord-core").versionRef(kord)
            library(
                "kord-ksp-annotations",
                "dev.kord",
                "kord-ksp-annotations"
            ).versionRef("kord")
            library(
                "kord-ksp-processors",
                "dev.kord",
                "kord-ksp-processors"
            ).version("feature-native-SNAPSHOT")
            library("junit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").version("5.13.4")
            library("kotlinlogging", "io.github.oshai", "kotlin-logging").version("7.0.13")
            library("sl4fj-simple", "org.slf4j", "slf4j-simple").version("2.0.17")

            library("kotlinx-nodejs", "org.jetbrains.kotlin-wrappers", "kotlin-node").version("2025.9.8-24.3.1")

            library("lavalink-protocol", "dev.arbjerg.lavalink", "protocol").version("4.1.1")
            library("lyrics-protocol", "dev.schlaubi.lyrics", "protocol").version("2.6.1")
            library(
                "lavasearch-protocol",
                "com.github.topi314.lavasearch",
                "lavasearch-protocol"
            ).version("1.0.0")
            library(
                "lavasrc-protocol",
                "com.github.topi314.lavasrc",
                "protocol"
            ).version("4.8.1")

            library("kotlinpoet", "com.squareup", "kotlinpoet-ksp").version("2.2.0")

            library("codegen", "dev.kord.codegen", "kotlinpoet").versionRef(codegen)
            library("codegen-ksp", "dev.kord.codegen", "ksp").versionRef(codegen)
            library("codegen-ksp-annotations", "dev.kord.codegen", "ksp-annotations").versionRef(codegen)
            library("codegen-ksp-processor", "dev.kord.codegen", "ksp-processor").versionRef(codegen)
        }
    }
}

fun VersionCatalogBuilder.kotlinx() {
    val coroutines = version("coroutines", "1.10.2")
    library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutines)
    library("kotlinx-coroutines-jdk8", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef(coroutines)
    library("kotlinx-coroutines-jdk9", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk9").versionRef(coroutines)
    library("kotlinx-coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutines)
    library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.9.0")
    library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.7.1-0.6.x-compat")
    plugin("kotlinx-atomicfu", "org.jetbrains.kotlinx.atomicfu").version("0.29.0")
}

fun VersionCatalogBuilder.ktor() {
    val ktor = version("ktor", "3.3.0")
    library("ktor-io", "io.ktor", "ktor-io").versionRef(ktor)
    library("ktor-utils", "io.ktor", "ktor-utils").versionRef(ktor)
    library("ktor-client-websockets", "io.ktor", "ktor-client-websockets").versionRef(ktor)
    library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktor)
    library("ktor-client-resources", "io.ktor", "ktor-client-resources").versionRef(ktor)
    library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)
    library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(ktor)
    library("ktor-client-logging", "io.ktor", "ktor-client-logging").versionRef(ktor)
    library("ktor-client-okhttp", "io.ktor", "ktor-client-okhttp").versionRef(ktor)
    library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef(ktor)
    library("ktor-client-mock", "io.ktor", "ktor-client-mock").versionRef(ktor)
}

fun VersionCatalogBuilder.ksp() {
    val ksp = version("ksp", "2.2.20-2.0.3")
    library("ksp-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef(ksp)
    plugin("ksp", "com.google.devtools.ksp").versionRef(ksp)
}

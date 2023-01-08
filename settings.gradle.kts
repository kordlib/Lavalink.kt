@file:Suppress("UnstableApiUsage")

rootProject.name = "lavakord"
include("example")
include("kord") // GitHub Actions gets mad about this and I can't reproduce this locally
//include("jsExample") // kotlinx-nodejs is unavailable: https://github.com/Kotlin/kotlinx-nodejs/issues/16
include("core")
include("java")
include("jda")
include("jda-java")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    resolutionStrategy {
        repositories {
            gradlePluginPortal()
        }

        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            kotlinx()
            ktor()
            library("kord-core", "dev.kord", "kord-core").version("0.8.x-SNAPSHOT")
            library("junit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").version("5.9.1")
            library("kotlinlogging", "io.github.microutils", "kotlin-logging").version("3.0.4")
            library("sl4fj-simple", "org.slf4j", "slf4j-simple").version("2.0.6")
        }
    }
}

fun VersionCatalogBuilder.kotlinx() {
    val coroutines = version("coroutines", "1.6.4")
    library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutines)
    library("kotlinx-coroutines-jdk8", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef(coroutines)
    library("kotlinx-coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutines)
    library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.4.1")
    library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.4.0")
}

fun VersionCatalogBuilder.ktor() {
    val ktor = version("ktor", "2.2.2")
    library("ktor-io", "io.ktor", "ktor-io").versionRef(ktor)
    library("ktor-utils", "io.ktor", "ktor-utils").versionRef(ktor)
    library("ktor-client-websockets", "io.ktor", "ktor-client-websockets").versionRef(ktor)
    library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktor)
    library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)
    library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(ktor)
    library("ktor-client-logging", "io.ktor", "ktor-client-logging").versionRef(ktor)
    library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef(ktor)
    library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef(ktor)
    library("ktor-client-mock", "io.ktor", "ktor-client-mock").versionRef(ktor)
}

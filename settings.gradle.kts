@file:Suppress("UnstableApiUsage")

rootProject.name = "lavakord"
include("example")
include("kord") // GitHub Actions gets mad about this and I can't reproduce this locally
//include("jsExample") // kotlinx-nodejs is unavailable: https://github.com/Kotlin/kotlinx-nodejs/issues/16
include("core")
include("java")
include("jda")
include("jda-java")
include("bom")

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
            alias("kord-core").to("dev.kord", "kord-core").version("0.8.x-SNAPSHOT")
            alias("junit-jupiter-engine").to("org.junit.jupiter", "junit-jupiter-engine").version("5.8.2")
            alias("kotlinlogging").to("io.github.microutils", "kotlin-logging").version("2.1.21")
            alias("sl4fj-simple").to("org.slf4j", "slf4j-simple").version("1.7.30")
        }
    }
}

fun VersionCatalogBuilder.kotlinx() {
    val coroutines = version("coroutines", "1.6.0")
    alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(coroutines)
    alias("kotlinx-coroutines-jdk8").to("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef(coroutines)
    alias("kotlinx-coroutines-test").to("org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutines)
    alias("kotlinx-serialization-json").to("org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.3.2")
    alias("kotlinx-datetime").to("org.jetbrains.kotlinx", "kotlinx-datetime").version("0.3.1")
}

fun VersionCatalogBuilder.ktor() {
    val ktor = version("ktor", "1.6.3")
    alias("ktor-io").to("io.ktor", "ktor-io").versionRef(ktor)
    alias("ktor-utils").to("io.ktor", "ktor-utils").versionRef(ktor)
    alias("ktor-client-websockets").to("io.ktor", "ktor-client-websockets").versionRef(ktor)
    alias("ktor-client-core").to("io.ktor", "ktor-client-core").versionRef(ktor)
    alias("ktor-client-serialization").to("io.ktor", "ktor-client-serialization").versionRef(ktor)
    alias("ktor-client-logging").to("io.ktor", "ktor-client-logging").versionRef(ktor)
    alias("ktor-client-cio").to("io.ktor", "ktor-client-cio").versionRef(ktor)
    alias("ktor-client-js").to("io.ktor", "ktor-client-js").versionRef(ktor)
    alias("ktor-client-mock").to("io.ktor", "ktor-client-mock").versionRef(ktor)
}

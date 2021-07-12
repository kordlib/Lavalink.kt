import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

object Versions {
    const val kotlin = "1.5.20"
    const val ktor = "1.6.1"
    const val coroutines = "1.5.1"
    const val kotlinxSerialization = "1.2.2"
    const val kotlinLogging = "2.0.8"
    const val kord = "0.7.3"
    const val atomicFu = "0.16.2"

    const val junit5 = "5.7.2"
    const val junitJupiterEngine = junit5
    const val coroutinesTest = coroutines
    const val slf4j = "1.7.30"
}

object Dependencies {
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesJdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.coroutines}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesTest}"
    const val kotlinxSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"

    const val `ktor-io` = "io.ktor:ktor-io:${Versions.ktor}"
    const val `ktor-utils` = "io.ktor:ktor-utils:${Versions.ktor}"
    const val `ktor-client-websockets` = "io.ktor:ktor-client-websockets:${Versions.ktor}"
    const val `ktor-client-core` = "io.ktor:ktor-client-core:${Versions.ktor}"
    const val `ktor-client-serialization` = "io.ktor:ktor-client-serialization:${Versions.ktor}"
    const val `ktor-client-logging` = "io.ktor:ktor-client-logging:${Versions.ktor}"
    const val `ktor-client-cio` = "io.ktor:ktor-client-cio:${Versions.ktor}"
    const val `ktor-client-js` = "io.ktor:ktor-client-js:${Versions.ktor}"
    const val `ktor-client-mock` = "io.ktor:ktor-client-mock:${Versions.ktor}"

    const val kotlinLogging = "io.github.microutils:kotlin-logging:${Versions.kotlinLogging}"

    const val kord = "dev.kord:kord-core:${Versions.kord}"

    const val `junit-jupiter-engine` = "org.junit.jupiter:junit-jupiter-engine:${Versions.junitJupiterEngine}"

    const val slf4jSimple = "org.slf4j:slf4j-simple:${Versions.slf4j}"
}

fun RepositoryHandler.jitpack() = maven("https://jitpack.io")
fun RepositoryHandler.kord() = maven("https://dl.bintray.com/kordlib/Kord")
fun RepositoryHandler.sonatype() = maven("https://oss.sonatype.org/content/repositories/snapshots/")

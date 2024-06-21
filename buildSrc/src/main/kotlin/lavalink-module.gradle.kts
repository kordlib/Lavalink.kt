import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm()

    // See https://github.com/DRSchlaubi/Lavakord/issues/2
    js(IR) {
        nodejs()
        // browser() doesn't work because the js websocket client does not allowed you to set headers
        // Apart from that why would you need Lavalink in your browser?
        useCommonJs()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("dev.schlaubi.lavakord.PluginApi")
                optIn("dev.schlaubi.lavakord.UnsafeRestApi")
            }
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    `lavalink-module`
    `lavalink-publishing`
    kotlin("plugin.serialization")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            languageSettings.optIn("dev.schlaubi.lavakord.PluginApi")
            languageSettings.optIn("dev.schlaubi.lavakord.UnsafeRestApi")
        }
        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
            dependencies {
                api(projects.core)
                api(libs.lavasearch.protocol)

                implementation(libs.ktor.client.resources)
                implementation(libs.kord.ksp.annotations)
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(libs.kord.ksp.processors)
}

tasks {
    listOf(
        "sourcesJar",
        "jsSourcesJar",
        "jvmSourcesJar",
        "compileKotlinJs",
        "compileKotlinJvm",
    ).forEach {
        named(it) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml")))
}

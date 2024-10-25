import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    `lavalink-module`
    `lavalink-publishing`
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            languageSettings.optIn("dev.schlaubi.lavakord.PluginApi")
            languageSettings.optIn("dev.schlaubi.lavakord.UnsafeRestApi")
        }
        commonMain {
            dependencies {
                api(projects.core)
                implementation(libs.ktor.client.resources)
                api(libs.lyrics.protocol)
            }
        }
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml")))
}

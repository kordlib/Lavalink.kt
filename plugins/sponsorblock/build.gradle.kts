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
        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
            dependencies {
                api(projects.core)
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
        "dokkaGeneratePublicationHtml"
    ).forEach {
        named(it) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml")))
}

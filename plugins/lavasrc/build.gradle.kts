import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `lavalink-module`
    `lavalink-publishing`
    alias(libs.plugins.ksp)
}

kotlin {
    jvm {
        compilations.all {
            compilerOptions.configure {
                jvmTarget = JvmTarget.JVM_11
            }
        }
    }

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
                api(libs.lavasrc.protocol)
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(projects.plugins.kspProcessor)
}

tasks {
    listOf("sourcesJar", "jsSourcesJar", "jvmSourcesJar", "compileKotlinJs", "compileKotlinJvm", "dokkaHtml").forEach {
        named(it) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaHtml")))
}

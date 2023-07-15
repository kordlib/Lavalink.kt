plugins {
    `lavalink-module`
    `lavalink-publishing`
    kotlin("plugin.serialization")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("$buildDir/generated/ksp/metadata/commonMain/kotlin")
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
    listOf("sourcesJar", "jsSourcesJar", "jvmSourcesJar", "compileKotlinJs", "compileKotlinJvm", "dokkaHtml").forEach {
        named(it) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}


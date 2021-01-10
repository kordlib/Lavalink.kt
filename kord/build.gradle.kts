plugins {
    kotlin("multiplatform")
}

group = "dev.kord.extensions.lavalink"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api(project(":"))
            }
        }
        commonTest  {
            repositories {
                maven("https://jitpack.io")
                maven("https://oss.sonatype.org/content/repositories/snapshots")
                jcenter()
            }

            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("dev.kord", "kord-core", "0.7.0-SNAPSHOT")
            }
        }
    }
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.implementation(groupId: String, artifactId: String, version: String) = implementation("$groupId:$artifactId:$version")

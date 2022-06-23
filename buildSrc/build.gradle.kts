import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    `kotlin-dsl`
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
}

group = "me.schlaubi"
version = "3.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.7.0"))
    implementation(kotlin("serialization", version = "1.7.0"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.6.20")
    implementation(kotlin("gradle-plugin-api", version = "1.7.0"))
    implementation(gradleApi())
    implementation(localGroovy())
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            languageVersion = "1.5"
        }
    }
}

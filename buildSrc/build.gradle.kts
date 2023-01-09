import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    `kotlin-dsl`
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
}

group = "me.schlaubi"
version = "3.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugina-api"))
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("serialization"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.7.20")
    implementation(kotlin("gradle-plugin-api"))
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

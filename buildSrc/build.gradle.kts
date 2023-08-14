import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = "1.9.0"
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation(kotlin("gradle-plugin-api", kotlinVersion))
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.25.3")
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.8.20")
    implementation(gradleApi())
    implementation(localGroovy())
}

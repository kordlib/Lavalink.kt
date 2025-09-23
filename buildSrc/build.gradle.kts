plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    // Repo providing the Kord Gradle plugin
    maven("https://europe-west3-maven.pkg.dev/mik-music/kord")
}

dependencies {
    val kotlinVersion = "2.2.20"
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation(kotlin("gradle-plugin-api", kotlinVersion))
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "2.0.0")
    implementation("dev.kord", "gradle-tools", "1.10.2-SNAPSHOT")
    implementation(gradleApi())
    implementation(localGroovy())
}

plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = "2.0.0"
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation(kotlin("gradle-plugin-api", kotlinVersion))
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.29.0")
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.9.20")
    implementation(gradleApi())
    implementation(localGroovy())
}

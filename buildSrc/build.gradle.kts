plugins {
    groovy
    `kotlin-dsl`
}

group = "me.schlaubi"
version = "3.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.5.31"))
    implementation(kotlin("serialization", version = "1.5.31"))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", "1.5.30")
    implementation(kotlin("gradle-plugin-api", version = "1.5.30"))
    implementation(gradleApi())
    implementation(localGroovy())
}

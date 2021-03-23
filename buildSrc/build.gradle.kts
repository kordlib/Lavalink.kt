plugins {
    groovy
    `kotlin-dsl`
}

group = "me.schlaubi"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin-api", version = "1.4.21"))
    implementation(gradleApi())
    implementation(localGroovy())
}

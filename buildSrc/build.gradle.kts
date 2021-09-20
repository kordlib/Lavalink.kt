plugins {
    groovy
    `kotlin-dsl`
}

group = "me.schlaubi"
version = "2.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin-api", version = "1.5.30"))
    implementation(gradleApi())
    implementation(localGroovy())
}

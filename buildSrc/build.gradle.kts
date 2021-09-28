plugins {
    groovy
    `kotlin-dsl`
}

group = "me.schlaubi"
version = "2.1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin-api", version = "1.5.30"))
    implementation(gradleApi())
    implementation(localGroovy())
}

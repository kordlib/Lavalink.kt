plugins {
    kotlin("js")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(npm("discord.js", "14.7.1"))
    implementation(libs.kotlinx.nodejs)
}

kotlin {
    js(IR) {
        nodejs()
        binaries.executable()
    }
}

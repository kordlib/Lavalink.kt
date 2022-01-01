plugins {
    kotlin("js")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(npm("discord.js", "13.5.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7") // anything post 0.0.4 is bugged and doesn't compile
}

kotlin {
    js(IR) {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        all {
//            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.requiresOptIn)
        }
    }
}

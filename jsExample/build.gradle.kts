plugins {
    kotlin("js")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(root)
    implementation(Dependencies.coroutines)
    implementation(npm("discord.js", "12.5.1"))
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.4") // anything post 0.0.4 is bugged and doesn't compile
}

kotlin {
    js(IR) {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.requiresOptIn)
        }
    }
}

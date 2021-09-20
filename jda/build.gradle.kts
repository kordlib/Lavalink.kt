plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("org.jetbrains.dokka")
}

group = "dev.schlaubi.lavakord"
version = "2.0.1"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        jvmMain {
            repositories {
                maven("https://jitpack.io")
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                api(root)
                implementation("net.dv8tion:JDA:4.3.0_296") {
                    exclude(module = "opus-java")
                }
            }
        }
    }
}

applyPublishing()

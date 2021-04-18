plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "dev.schlaubi.lavakord"
version = "1.0.0-SNAPSHOT"

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
                jcenter()
                maven("https://jitpack.io")
            }

            dependencies {
                api(root)
                implementation(kotlin("stdlib"))
                implementation("net.dv8tion:JDA:4.2.1_253") {
                    exclude(module = "opus-java")
                }
            }
        }
    }
}

applyPublishing()

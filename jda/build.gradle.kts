plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "dev.schlaubi.lavakord"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
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
                implementation("net.dv8tion:JDA:4.2.0_228") {
                    exclude(module = "opus-java")
                }
            }
        }
    }
}

applyPublishing()

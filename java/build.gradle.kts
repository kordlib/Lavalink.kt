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
        all {
            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.requiresOptIn)
        }

        commonMain {
            dependencies {
                api(root)
                api(project(":jda"))
            }
        }
        jvmMain {
            kotlin.srcDir("main")
            repositories {
                jcenter()
                maven("https://jitpack.io")
            }

            dependencies {
                api(root)
                api(project(":jda"))
                api("net.dv8tion:JDA:4.2.0_228") {
                    exclude(module = "opus-java")
                }
                implementation(Dependencies.coroutinesJdk8)
                implementation(kotlin("stdlib"))
            }
        }
    }
}

applyPublishing()
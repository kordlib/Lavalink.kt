plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "dev.schlaubi.lavakord"
version = "2.0.0"

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
                mavenCentral()
                maven("https://jitpack.io")
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                api(root)
                api(project(":jda"))
                api("net.dv8tion:JDA:4.2.1_253") {
                    exclude(module = "opus-java")
                }
                implementation(Dependencies.coroutinesJdk8)
            }
        }
    }
}

applyPublishing()

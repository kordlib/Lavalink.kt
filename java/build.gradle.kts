plugins {
    `lavalink-module`
    `lavalink-publishing`
    id("org.jetbrains.dokka")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                api(projects.jda)
            }
        }
        jvmMain {
            repositories {
                mavenCentral()
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                api(projects.core)
                api(projects.jda)
                api("net.dv8tion:JDA:4.2.1_253") {
                    exclude(module = "opus-java")
                }
                implementation(libs.kotlinx.coroutines.jdk8)
            }
        }
    }
}

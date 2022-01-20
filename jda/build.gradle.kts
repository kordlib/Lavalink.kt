plugins {
    `lavalink-module`
    `lavalink-publishing`
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(projects.core)
                api(libs.kotlinlogging)
                api(libs.kotlinx.coroutines.jdk8)
                implementation("net.dv8tion:JDA:5.0.0-alpha.4") {
                    exclude(module = "opus-java")
                }
            }
        }
    }
}

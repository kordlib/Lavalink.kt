plugins {
    `lavalink-module`
    `lavalink-publishing`
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(projects.core)
                api(libs.kotlinlogging)
                api(libs.kotlinx.coroutines.jdk8)
                implementation("net.dv8tion:JDA:5.0.0-beta.6") {
                    exclude(module = "opus-java")
                }
            }
        }
    }
}

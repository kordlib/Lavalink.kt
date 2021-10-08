plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }

        tasks {
            withType<Test> {
                useJUnitPlatform()
            }
        }
    }

    // See https://github.com/DRSchlaubi/Lavakord/issues/2
    js(IR) {
        nodejs()
        // browser() doesn't work because the js websocket client does not allowed you to set headers
        // Apart from that why would you need Lavalink in your browser?
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}

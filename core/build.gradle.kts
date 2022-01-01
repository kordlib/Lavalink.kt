plugins {
    `lavalink-module`
    `lavalink-publishing`
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("kotlinx-atomicfu")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.datetime)

                implementation(libs.ktor.io)
                implementation(libs.ktor.utils)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.logging)

                implementation(libs.kotlinlogging)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit5"))
                runtimeOnly(libs.junit.jupiter.engine)
                runtimeOnly(libs.sl4fj.simple)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.ktor.client.js)

            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

plugins {
    `lavalink-module`
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("kotlinx-atomicfu")
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(Dependencies.coroutines)
                api(Dependencies.kotlinxSerialization)
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.0")

                implementation(Dependencies.`ktor-io`)
                implementation(Dependencies.`ktor-utils`)
                implementation(Dependencies.`ktor-client-websockets`)
                implementation(Dependencies.`ktor-client-core`)
                implementation(Dependencies.`ktor-client-serialization`)
                implementation(Dependencies.`ktor-client-logging`)

                implementation(Dependencies.kotlinLogging)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Dependencies.`ktor-client-mock`)
            }
        }

        jvmMain {
            dependencies {
                implementation(Dependencies.`ktor-client-cio`)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit5"))
                runtimeOnly(Dependencies.`junit-jupiter-engine`)
                runtimeOnly(Dependencies.slf4jSimple)
                implementation(Dependencies.coroutinesTest)
            }
        }

        jsMain {
            dependencies {
                implementation(Dependencies.`ktor-client-js`)

            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

applyPublishing()

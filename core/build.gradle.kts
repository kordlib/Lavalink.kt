plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    id("org.jetbrains.dokka")
    `maven-publish`
}

version = "1.0.0-SNAPSHOT"

sourceSets

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = Jvm.target
            }
        }

        tasks {
            withType<Test> {
                useJUnitPlatform()
            }
        }
    }

    // See https://github.com/DRSchlaubi/Lavakord/issues/2
//    js(IR) {
//        nodejs()
//        // browser() doesn't work because the js websocket client does not allowed you to set headers
//        // Apart from that why would you need Lavalink in your browser?
//    }

    sourceSets {
        all {

            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.experimentalTime)
            repositories {
                jcenter()
            }
        }

        commonMain {
            dependencies {
                api(Dependencies.coroutines)
                api(Dependencies.kotlinxSerialization)

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
            repositories {
                jitpack()
            }

            dependencies {
                implementation(Dependencies.`ktor-client-cio`)
                implementation("com.github.FredBoat:Lavalink-Client:4.0")
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

//        jsMain {
//            dependencies {
//                implementation(Dependencies.`ktor-client-js`)
//
//            }
//        }
//
//        jsTest {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
    }

    applyPublishing()
}

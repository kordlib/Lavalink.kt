plugins {
    `maven-publish`
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.jfrog.bintray") version Versions.bintray
    id("org.jetbrains.dokka") version "1.4.20"
    id("kotlinx-atomicfu") version Versions.atomicFu
}

group = "dev.kord.x"
version = "1.0.0-SNAPSHOT"

repositories {
    sonatype()
    jcenter()
}

kotlin {
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
    js(BOTH) {
        nodejs()
        // browser() doesn't work because the js websocket client does not allowe you to set headers
        // Apart from that why would you need Lavalink in your brwoser
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.requiresOptIn)
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

    publishing {
        publications {

        }
    }
}

tasks {
    task("runAllTests") {
        dependsOn(named("jvmTest"), named("jsLegacyTest"))
    }
}

publishing {
    repositories {
        maven {
            setUrl("https://api.bintray.com/maven/drschlaubi/maven/lavakord/;publish=1;override=0")

            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_KEY")
            }
        }
    }

    publications {
        filterIsInstance<MavenPublication>().forEach { publication ->
            publication.pom {
                name.set(project.name)
                description.set("Extension of the official LavaLink-Client to work with Kord")
                url.set("https://github.com/DRSchlaubi/lavakord")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/DRSchlaubi/Lavakord/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        name.set("Michael Rittmeister")
                        email.set("mail@schlaubi.me")
                        organizationUrl.set("https://michael.rittmeister.in")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/DRSchlaubi/lavakord.git")
                    developerConnection.set("scm:git:https://github.com/DRSchlaubi/lavakord.git")
                    url.set("https://github.com/DRSchlaubi/lavakord")
                }
            }
        }
    }
}

tasks {
    withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
        outputDirectory.set(file("docs/"))

        dokkaSourceSets {
            configureEach {
                includeNonPublic.set(false)

                perPackageOption {
                    matchingRegex.set(".*\\.internal.*") // will match all .internal packages and sub-packages
                    suppress.set(true)
                }
            }

//            named("jsMain") {
//                displayName.set("JS")
//            }

            named("jvmMain") {
                jdkVersion.set(8)
                displayName.set("JVM")
            }
        }
    }
}

kotlin {
    explicitApi()
}

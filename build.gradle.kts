plugins {
    kotlin("multiplatform") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("com.jfrog.bintray") version "1.8.5"
    id("org.jetbrains.dokka") version "1.4.20"
    `maven-publish`
}

group = "me.schlaubi"
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
    }

// See https://github.com/DRSchlaubi/Lavakord/issues/2
//    js {
//        nodejs()
//        browser()
//    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.requiresOptIn)
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

        jvmMain {
            repositories {
                jitpack()
            }

            dependencies {
                implementation(Dependencies.`ktor-client-cio`)
            }
        }

//        jsMain {
//            dependencies {
//                implementation(Dependencies.`ktor-client-js`)
//            }
//        }
    }

    publishing {
        publications {

        }
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

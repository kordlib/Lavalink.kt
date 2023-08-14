plugins {
    java
    kotlin("multiplatform")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
}

kotlin {
    jvmToolchain(19)
    jvm {
        withJava()
    }
    js(IR) {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kord.core)
                implementation(projects.kord)
                implementation(projects.plugins.lavasrc)
                implementation(projects.plugins.sponsorblock)
            }
        }

        named("jvmMain") {
            dependencies {
                implementation(projects.jdaJava)
                implementation(libs.sl4fj.simple)
                implementation(libs.kord.core)

                implementation("org.apache.groovy:groovy-all:4.0.7")
                implementation("org.jetbrains.kotlinx:atomicfu:0.20.2")

                implementation("net.dv8tion:JDA:5.0.0-beta.12") {
                    exclude(module = "opus-java")
                }
            }
        }

        named("jsMain") {
            dependencies {
                implementation(libs.kotlinx.nodejs)
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview")
    }
}

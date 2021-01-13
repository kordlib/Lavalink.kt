plugins {
    id("org.jetbrains.kotlin.js")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":jsMain"))
    implementation("io.ktor", "ktor-client-js", "1.5.0")
    implementation("io.ktor", "ktor-client-websockets", "1.5.0")
}

kotlin {
    js {
        nodejs {
        }
        binaries.executable()
    }
}
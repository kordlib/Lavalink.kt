plugins {
    `lavalink-module`
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

kotlin {
    sourceSets {
        jvmMain {
            repositories {
                maven("https://jitpack.io")
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                api(root)
                implementation("net.dv8tion:JDA:4.3.0_296") {
                    exclude(module = "opus-java")
                }
            }
        }
    }
}

applyPublishing()

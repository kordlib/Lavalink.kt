plugins {
    `lavalink-module`
    id("org.jetbrains.dokka")
}

kotlin {
    sourceSets {
        jvmMain {
            repositories {
                mavenCentral()
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                api(project(":jda"))
                api(project(":java"))
            }
        }
    }
}

applyPublishing()

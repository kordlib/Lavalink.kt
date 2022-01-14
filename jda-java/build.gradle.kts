plugins {
    `lavalink-module`
    `lavalink-publishing`
}

kotlin {
    sourceSets {
        jvmMain {
            repositories {
                mavenCentral()
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                api(projects.jda)
                api(projects.java)
            }
        }
    }
}

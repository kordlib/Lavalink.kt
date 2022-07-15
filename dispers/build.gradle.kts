plugins {
    `lavalink-module`
    `lavalink-publishing`
}

repositories {
    maven {
        name = "ZeroTwo Public Snapshots"
        url = uri("https://nexus.zerotwo.bot/repository/m2-public-snapshots/")
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvmMain {
            dependencies {
                implementation("dev.bitflow.dispers:dispers-client-kt:1.0-SNAPSHOT")
            }
        }
    }
}

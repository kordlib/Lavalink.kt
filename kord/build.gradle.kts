plugins {
    `lavalink-module`
    `lavalink-publishing`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
                implementation(libs.kord.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

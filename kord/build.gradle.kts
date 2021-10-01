plugins {
    `lavalink-module`
    `lavalink-publishing`
    id("org.jetbrains.dokka")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(root)
            }
        }

        commonTest  {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvmMain {
            dependencies {
                implementation(Dependencies.kord)
            }
        }
    }
}

applyPublishing()

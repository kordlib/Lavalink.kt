plugins {
    `lavalink-module`
    `lavalink-publishing`
}

repositories {
    mavenLocal()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
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
                implementation(libs.dispers)
            }
        }
    }
}

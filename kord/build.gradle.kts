plugins {
    kotlin("multiplatform")
}

group = "dev.kord.extensions.lavalink"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(root)
            }
        }
        commonTest  {
            repositories {
                sonatype()
                jcenter()
            }

            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvmMain {
            repositories  {
                jitpack()
            }

            dependencies {
                implementation(Dependencies.kord)
            }
        }
    }
}

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
}

version = "2.0.1"

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
        all {
            languageSettings.useExperimentalAnnotation(ExpermientalAnnotations.requiresOptIn)
        }

        commonMain {
            dependencies {
                api(root)
            }
        }

        commonTest  {
            repositories {
                mavenCentral()
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

applyPublishing()

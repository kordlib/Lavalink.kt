plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
}

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

applyPublishing()

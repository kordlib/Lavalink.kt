plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "dev.schlaubi.lavakord"
version = "1.0.0-SNAPSHOT"


kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        jvmMain {
            repositories {
                mavenCentral()
                maven("https://m2.dv8tion.net/releases")
            }

            dependencies {
                implementation(kotlin("stdlib"))
                api(project(":jda"))
                api(project(":java"))
            }
        }
    }
}

applyPublishing()

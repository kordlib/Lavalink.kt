rootProject.name = "lavakord"
//include("example")
include("kord") // GitHub Actions gets mad about this and I can't reproduce this locally
//include("jsExample") // kotlinx-nodejs is unavailable: https://github.com/Kotlin/kotlinx-nodejs/issues/16
include("core")
include("java")
include("jda")
include("jda-java")
include("bom")


enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    resolutionStrategy {
        repositories {
            gradlePluginPortal()
        }

        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

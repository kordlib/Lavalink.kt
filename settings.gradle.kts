rootProject.name = "lavakord"
include("example")
include("kord")
//include("jsExample") // kotlinx-nodejs is unavailable: https://github.com/Kotlin/kotlinx-nodejs/issues/16
include("core")
include("java")
include("jda")
include("jda-java")
include("bom")

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

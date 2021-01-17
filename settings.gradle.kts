rootProject.name = "lavakord"
include("example")
include("kord")
include("jsExample")
include("core")

pluginManagement {
    resolutionStrategy {
        repositories {
            jcenter()
            gradlePluginPortal()
        }

        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

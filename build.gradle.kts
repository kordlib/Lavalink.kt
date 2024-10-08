import dev.kord.gradle.tools.KordExtension
import dev.kord.gradle.tools.KordGradlePlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.dokka")
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.gradle.tools) apply false
}

group = "dev.schlaubi.lavakord"
version = "7.1.0"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.topi.wtf/snapshots")
        maven("https://maven.topi.wtf/releases")
    }
}

subprojects {
    afterEvaluate {
        apply<KordGradlePlugin>()
        configure<KordExtension> {
            publicationName = "mavenCentral"
            if (jvmTarget.get() <= JvmTarget.JVM_17) {
                jvmTarget = JvmTarget.JVM_17
            }
        }
    }
    group = rootProject.group

    tasks {
        withType<DokkaTask>().configureEach {
            dokkaSourceSets {
                configureEach {
                    includeNonPublic = false

                    perPackageOption {
                        matchingRegex = ".*\\.internal.*" // will match all .internal packages and sub-packages
                        suppress = true
                    }
                }
            }
        }
    }
}

// Use system Node.Js on NixOS
if (System.getenv("NIX_PROFILES") != null) {
    rootProject.plugins.withType<NodeJsRootPlugin> {
        rootProject.the<NodeJsRootExtension>().download = false
    }
}

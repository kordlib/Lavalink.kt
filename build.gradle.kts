import dev.kord.gradle.tools.KordExtension
import dev.kord.gradle.tools.KordGradlePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

plugins {
    id("org.jetbrains.dokka")
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.gradle.tools) apply false
}

group = "dev.schlaubi.lavakord"
version = "8.0.0"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.topi.wtf/snapshots")
        maven("https://maven.topi.wtf/releases")
    }
}

dependencies {
    dokka(projects.core)
    dokka(projects.java)
    dokka(projects.jda)
    dokka(projects.jdaJava)
    dokka(projects.kord)
    dokka(projects.plugins.lavasearch)
    dokka(projects.plugins.lavasrc)
    dokka(projects.plugins.lyrics)
    dokka(projects.plugins.sponsorblock)
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

        if (plugins.hasPlugin("org.jetbrains.dokka")) {
            dokka {
                dokkaSourceSets {
                    configureEach {
                        perPackageOption {
                            matchingRegex = ".*\\.internal.*" // will match all .internal packages and sub-packages
                            suppress = true
                        }
                    }
                }
            }
        }
    }

    group = rootProject.group

}

// Use system Node.Js on NixOS
if (System.getenv("NIX_PROFILES") != null) {
    rootProject.plugins.withType<NodeJsRootPlugin> {
        rootProject.the<NodeJsRootExtension>().download = false
    }
}

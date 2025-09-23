import dev.kord.gradle.tools.KordExtension
import dev.kord.gradle.tools.KordGradlePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

plugins {
    org.jetbrains.dokka
    alias(libs.plugins.kotlinx.atomicfu) apply false
}

group = "dev.schlaubi.lavakord"

allprojects {
    repositories {
        mavenCentral()
        maven("https://snapshots.kord.dev")
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.topi.wtf/snapshots")
        maven("https://maven.topi.wtf/releases")
    }

    configurations.all {
        val conf = this
        conf.resolutionStrategy.eachDependency {
            if (requested.group == "dev.kord.codegen") {
                useVersion("1.0.0")
            }
        }
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
            if (jvmTarget.get() <= JvmTarget.JVM_17) {
                jvmTarget = JvmTarget.JVM_21
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
    rootProject.plugins.withType<NodeJsPlugin> {
        rootProject.the<NodeJsEnvSpec>().download = false
    }
}

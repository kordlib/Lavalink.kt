import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

plugins {
    id("org.jetbrains.dokka")
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.git.publish)
}

group = "dev.schlaubi.lavakord"
version = "6.1.1"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.topi.wtf/snapshots")
        maven("https://maven.topi.wtf/releases")
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory = rootProject.file("docs")
    }

    gitPublishCopy {
        dependsOn(dokkaHtmlMultiModule)
    }
}

configure<GitPublishExtension> {
    repoUri = "https://github.com/DRSchlaubi/lavakord.git"
    branch = "gh-pages"

    contents {
        from(file("docs"))
        from(file("CNAME"))
    }

    commitMessage = "Update Docs"
}

subprojects {
    version = libraryVersion
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

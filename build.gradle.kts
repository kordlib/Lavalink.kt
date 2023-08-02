import org.ajoberstar.gradle.git.publish.GitPublishExtension

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("org.jetbrains.dokka")
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.git.publish)
}

group = "dev.schlaubi.lavakord"
version = "5.1.5"

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.topi.wtf/snapshots")
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
    }

    commitMessage = "Update Docs"
}

subprojects {
    group = rootProject.group

    tasks {
        withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
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

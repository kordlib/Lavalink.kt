import org.ajoberstar.gradle.git.publish.GitPublishExtension

plugins {
    `maven-publish`
    kotlin("multiplatform") version Versions.kotlin apply false
    kotlin("plugin.serialization") version Versions.kotlin apply false
    id("com.jfrog.bintray") version Versions.bintray apply false
    id("org.jetbrains.dokka") version "1.4.20"
    id("kotlinx-atomicfu") version Versions.atomicFu apply false
    id("org.ajoberstar.git-publish") version "2.1.3"
}

group = "dev.schlaubi.lavakord"
version = "1.0.0-SNAPSHOT"

allprojects {
    repositories {
        sonatype()
        jcenter()
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory.set(rootProject.file("docs"))
    }

    val docs = task<Copy>("createDocsIndex") {
        dependsOn(dokkaHtmlMultiModule)
        val outputDirectory = dokkaHtmlMultiModule.get().outputDirectory.get()
        from(outputDirectory)
        include("-modules.html")
        into(outputDirectory)

        rename("-modules.html", "index.html")
    }

    gitPublishPush {
        dependsOn(docs)
    }
}

configure<GitPublishExtension> {
    repoUri.set("https://github.com/DRSchlaubi/lavakord.git")
    branch.set("gh-pages")

    contents {
        from(file("docs"))
    }

    commitMessage.set("Update Docs")
}

subprojects {
    group = rootProject.group

    tasks {
        withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
            dokkaSourceSets {
                configureEach {
                    includeNonPublic.set(false)

                    perPackageOption {
                        matchingRegex.set(".*\\.internal.*") // will match all .internal packages and sub-packages
                        suppress.set(true)
                    }
                }

                if (asMap.containsKey("jsMain")) {
                    named("jsMain") {
                        displayName.set("JS")
                    }
                }

                if (asMap.containsKey("jvmMain")) {
                    named("jvmMain") {
                        jdkVersion.set(8)
                        displayName.set("JVM")
                    }
                }
            }
        }
    }
}

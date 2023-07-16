plugins {
    `maven-publish`
    signing
}

fun MavenPublication.addDokkaIfNeeded() {
    if (tasks.findByName("dokkaHtml") != null) {
        val platform = name.substringAfterLast('-')
        val dokkaJar = tasks.register("${platform}DokkaJar", Jar::class) {
            dependsOn("dokkaHtml")
            archiveClassifier = "javadoc"
            destinationDirectory = buildDir.resolve(platform)
            from(tasks.getByName("dokkaHtml"))
        }
        artifact(dokkaJar)
    }
}

publishing {
    repositories {
        listOf(
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/",
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        ).forEach {
            maven {
                setUrl(it)
                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_KEY")
                }
            }
        }
    }

    publications {
        withType<MavenPublication> {
            addDokkaIfNeeded()
            pom {
                name = project.name
                description = "Coroutine based client for Lavalink (Kotlin and Java)"
                url = "https://github.com/DRSchlaubi/Lavalink.kt"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/DRSchlaubi/Lavalink.kt/blob/main/LICENSE"
                    }
                }

                developers {
                    developer {
                        name = "Michael Rittmeister"
                        email = "mail@schlaubi.me"
                        organizationUrl = "https://michael.rittmeister.in"
                    }
                }

                scm {
                    connection = "scm:git:https://github.com/DRSchlaubi/Lavalink.kt.git"
                    developerConnection = "scm:git:https://github.com/DRSchlaubi/Lavalin.kt.git"
                    url = "https://github.com/DRSchlaubi/Lavalink.kt"
                }
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey")?.toString()
    val signingPassword = findProperty("signingPassword")?.toString()
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(
            String(java.util.Base64.getDecoder().decode(signingKey.toByteArray())),
            signingPassword
        )

        publishing.publications.withType<MavenPublication> {
            sign(this)
        }
    }
}

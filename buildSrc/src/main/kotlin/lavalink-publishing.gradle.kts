plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            setUrl("https://schlaubi.jfrog.io/artifactory/lavakord")
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_KEY")
            }
        }
    }

    publications {
        withType<MavenPublication>() {
            pom {
                name.set(project.name)
                description.set("Coroutine based client for Lavalink (Kotlin and Java)")
                url.set("https://github.com/DRSchlaubi/Lavalink.kt")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/DRSchlaubi/Lavalink.kt/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        name.set("Michael Rittmeister")
                        email.set("mail@schlaubi.me")
                        organizationUrl.set("https://michael.rittmeister.in")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/DRSchlaubi/Lavalink.kt.git")
                    developerConnection.set("scm:git:https://github.com/DRSchlaubi/Lavalink.kt.git")
                    url.set("https://github.com/DRSchlaubi/Lavalink.kt")
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
    }

    publishing.publications.withType<MavenPublication> {
        sign(this)
    }
}

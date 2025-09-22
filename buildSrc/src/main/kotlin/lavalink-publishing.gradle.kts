
plugins {
    id("com.vanniktech.maven.publish.base")
    `maven-publish`
}

mavenPublishing {
    publishToMavenCentral(true)
    signAllPublications()

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

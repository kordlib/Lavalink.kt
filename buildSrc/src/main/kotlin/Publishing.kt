import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

fun Project.applyPublishing() {
    val configure: PublishingExtension.() -> Unit = {
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
            filterIsInstance<MavenPublication>().forEach { publication ->
                publication.pom {
                    name.set(project.name)
                    description.set("Extension of the official LavaLink-Client to work with Kord")
                    url.set("https://github.com/DRSchlaubi/lavakord")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/DRSchlaubi/Lavakord/blob/master/LICENSE")
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
                        connection.set("scm:git:https://github.com/DRSchlaubi/lavakord.git")
                        developerConnection.set("scm:git:https://github.com/DRSchlaubi/lavakord.git")
                        url.set("https://github.com/DRSchlaubi/lavakord")
                    }
                }
            }
        }
    }

    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("publishing", configure)
}
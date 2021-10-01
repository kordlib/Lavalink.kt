plugins {
    `lavalink-publishing`
    `java-platform`
    `maven-publish`
}

val me = project

// Without Gradle won't find any project with the publish plugin applied
javaPlatform.allowDependencies()
rootProject.subprojects {
    if (name != me.name) {
        me.evaluationDependsOn(path)
    }
}

dependencies {
    constraints {
        rootProject.subprojects.forEach {
            if (it.plugins.hasPlugin("maven-publish") && it.name != name) {
                it.publishing.publications.all {
                    if (this is MavenPublication) {
                        if (!artifactId.endsWith("-metadata") &&
                            !artifactId.endsWith("-kotlinMultiplatform")
                        ) {
                            api(groupId, artifactId, version)
                        }
                    }
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("javaPlatform"))
        }
    }
}

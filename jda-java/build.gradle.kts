import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    `lavalink-jvm-module`
    `lavalink-publishing`
}

dependencies {
    api(projects.jda)
    api(projects.java)
}

mavenPublishing {
    configure(KotlinJvm(JavadocJar.Dokka("dokkaGeneratePublicationHtml")))
}

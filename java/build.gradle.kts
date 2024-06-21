import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    `lavalink-jvm-module`
    `lavalink-publishing`
}

dependencies {
    api(projects.core)
    api(projects.jda) {
        exclude(module = "opus-java")
    }
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.kotlinx.coroutines.jdk9)
}


mavenPublishing {
    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml")))
}

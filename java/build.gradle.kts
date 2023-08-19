import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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


kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

mavenPublishing {
    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml")))
}

plugins {
    `lavalink-jvm-module`
    `lavalink-publishing`
}

repositories {
    mavenCentral()
}

dependencies {
    api(projects.core)
    api(libs.kotlinlogging)
    api(libs.kotlinx.coroutines.jdk8)
     api("net.dv8tion:JDA:5.0.0-beta.18") {
        exclude(module = "opus-java")
    }
}

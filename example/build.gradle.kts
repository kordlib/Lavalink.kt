plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    jcenter()
}

dependencies {
    implementation(project(":kord"))
    implementation(project(":jda-java"))
    implementation("org.slf4j", "slf4j-simple", "1.7.30")

    implementation(Dependencies.kord)

    implementation("dev.kord.x:commands-runtime-kord:0.4.0-SNAPSHOT")
    implementation("org.codehaus.groovy", "groovy-all", "2.4.15")
    kapt("dev.kord.x:commands-processor:0.4.0-SNAPSHOT")

    implementation("net.dv8tion:JDA:4.2.1_253") {
        exclude(module = "opus-java")
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

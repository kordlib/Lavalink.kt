plugins {
    java
    kotlin("jvm")
    kotlin("kapt")
}

group = "me.schlaubi.lavakord"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
    mavenCentral()
}

dependencies {
    implementation(projects.kord)
    implementation(projects.jdaJava)
    implementation(libs.sl4fj.simple)
    implementation(libs.kord.core)

    implementation("dev.kord.x:commands-runtime-kord:0.4.0-SNAPSHOT")
    implementation("org.codehaus.groovy", "groovy-all", "2.4.15")
    kapt("dev.kord.x:commands-processor:0.4.0-SNAPSHOT")

    implementation("net.dv8tion:JDA:5.0.0-alpha.3") {
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

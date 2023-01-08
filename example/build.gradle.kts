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

    implementation("org.apache.groovy:groovy-all:4.0.7")
    kapt("dev.kord.x:commands-processor:0.4.0-SNAPSHOT")

    implementation("net.dv8tion:JDA:5.0.0-alpha.3") {
        exclude(module = "opus-java")
    }
}

kotlin {
    jvmToolchain(19)
}

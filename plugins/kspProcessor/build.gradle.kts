import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")

    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.core)
    implementation(libs.codegen)
    implementation(libs.codegen.ksp)
    implementation(libs.kotlinpoet)
    implementation(libs.ksp.api)
    implementation(kotlin("reflect"))
    ksp(libs.codegen.ksp.processor)
}

kotlin {
    compilerOptions {
        optIn.add("dev.schlaubi.lavakord.PluginApi")
        freeCompilerArgs.add("-Xcontext-receivers")
    }

    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

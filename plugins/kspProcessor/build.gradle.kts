plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlinpoet)
    implementation(libs.ksp.api)
    implementation(kotlin("reflect"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

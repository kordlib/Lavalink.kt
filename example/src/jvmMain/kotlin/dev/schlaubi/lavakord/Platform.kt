package dev.schlaubi.lavakord

actual fun getEnv(name: String): String? = System.getenv(name)

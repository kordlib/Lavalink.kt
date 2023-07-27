package dev.schlaubi.lavakord

import node.process.process

actual fun getEnv(name: String) = process.env[name]

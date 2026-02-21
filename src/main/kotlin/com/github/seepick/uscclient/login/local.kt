package com.github.seepick.uscclient.login

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.io.File
import java.util.Properties

private val log = logger {}

internal val localCredsFile = File(".local-usc-credentials.properties")

public fun Credentials.Companion.loadLocal(): Credentials {
    val syspropUsername = System.getProperty("username")
    val syspropPassword = System.getProperty("password")
    if (syspropUsername != null && syspropPassword != null) {
        log.info { "Using credentials from system property." }
        return Credentials(syspropUsername, syspropPassword)
    }

    if (localCredsFile.exists()) {
        log.info { "Using credentials from local file ${localCredsFile.absolutePath}" }
        val props = Properties().also {
            it.load(localCredsFile.reader().buffered())
        }
        return Credentials(props.getProperty("username"), props.getProperty("password"))
    }
    error("No credentials provided via sys-props or in: ${localCredsFile.canonicalPath}.")
}

package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger

interface UscConnector {
    fun connect(): UscClient
}

interface UscClient {
    fun getFoo(): String
}

internal class UscConnectorImpl : UscConnector {
    private val log = logger {}
    override fun connect(): UscClient {
        log.debug { "connect()" }
        return UscClientImpl()
    }
}

internal class UscClientImpl : UscClient {
    private val log = logger {}
    override fun getFoo(): String {
        log.debug { "connect()" }
        return "foo usc response"
    }
}


package com.github.seepick.uscclient.login

import io.ktor.client.statement.HttpResponse
import io.ktor.http.setCookie

@JvmInline
public value class PhpSessionId(val value: String) {
    override fun toString() = value
}

internal val HttpResponse.phpSessionId: PhpSessionId
    get() = setCookie().singleOrNull { it.name == "PHPSESSID" }?.value?.let { PhpSessionId(it) }
        ?: error("PHPSESSID cookie is not set!")

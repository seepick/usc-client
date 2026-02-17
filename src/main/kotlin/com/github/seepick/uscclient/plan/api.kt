package com.github.seepick.uscclient.plan

import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText

internal interface MembershipApi {
    suspend fun fetch(session: PhpSessionId): Membership
}

internal class MembershipHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
) : MembershipApi {

    private val log = logger {}

    override suspend fun fetch(session: PhpSessionId): Membership {
        log.debug { "fetch()" }
        val response = http.safeGet("/profile/membership") {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Membership")
        return MembershipParser.parse(response.bodyAsText())
    }
}

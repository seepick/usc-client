package com.github.seepick.uscclient.plan

import com.github.seepick.uscclient.City
import com.github.seepick.uscclient.Country
import com.github.seepick.uscclient.PhpSessionId
import com.github.seepick.uscclient.Plan
import com.github.seepick.uscclient.ResponseStorage
import com.github.seepick.uscclient.UscConfig
import com.github.seepick.uscclient.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url

interface MembershipApi {
    suspend fun fetch(session: PhpSessionId): Membership
}

class MembershipHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
    uscConfig: UscConfig,
) : MembershipApi {

    private val log = logger {}
    private val baseUrl = uscConfig.baseUrl

    override suspend fun fetch(session: PhpSessionId): Membership {
        log.debug { "fetch()" }
        val response = http.safeGet(Url("$baseUrl/profile/membership")) {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Membership")
        return MembershipParser.parse(response.bodyAsText())
    }
}

data class Membership(
    val plan: Plan,
    val country: Country,
    val city: City,
)

package seepick.localsportsclub.api.plan

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import seepick.localsportsclub.api.PhpSessionId
import seepick.localsportsclub.api.ResponseStorage
import seepick.localsportsclub.api.UscConfig
import seepick.localsportsclub.service.model.City
import seepick.localsportsclub.service.model.Country
import seepick.localsportsclub.service.model.Plan
import seepick.localsportsclub.service.safeGet

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

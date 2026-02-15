package seepick.localsportsclub.api.checkin

import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import seepick.localsportsclub.api.PhpSessionId
import seepick.localsportsclub.api.ResponseStorage
import seepick.localsportsclub.api.UscConfig
import seepick.localsportsclub.service.date.Clock
import seepick.localsportsclub.service.safeGet

interface CheckinApi {
    suspend fun fetchPage(session: PhpSessionId, pageNr: Int): CheckinsPage
}

class CheckinHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
    private val clock: Clock,
    uscConfig: UscConfig,
) : CheckinApi {

    private val baseUrl = uscConfig.baseUrl

    override suspend fun fetchPage(session: PhpSessionId, pageNr: Int): CheckinsPage {
        val response = http.safeGet(Url("$baseUrl/profile/check-ins")) {
            parameter("page", pageNr)
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Checkin-$pageNr")
        return CheckinsParser.parse(response.bodyAsText(), clock.today())
    }
}

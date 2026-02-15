package seepick.localsportsclub.api.schedule

import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import seepick.localsportsclub.api.PhpSessionId
import seepick.localsportsclub.api.ResponseStorage
import seepick.localsportsclub.api.UscConfig
import seepick.localsportsclub.service.safeGet

interface ScheduleApi {
    suspend fun fetchScheduleRows(session: PhpSessionId): List<ScheduleRow>
}

class ScheduleHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
    uscConfig: UscConfig,
) : ScheduleApi {

    private val baseUrl = uscConfig.baseUrl

    override suspend fun fetchScheduleRows(session: PhpSessionId): List<ScheduleRow> {
        val response = http.safeGet(Url("$baseUrl/profile/schedule")) {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Schedule")
        return ScheduleParser.parse(response.bodyAsText()).rows
    }
}

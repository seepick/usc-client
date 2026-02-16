package com.github.seepick.uscclient.schedule

import com.github.seepick.uscclient.UscConfig
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.safeGet
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url

internal interface ScheduleApi {
    suspend fun fetchScheduleRows(session: PhpSessionId): List<ScheduleRow>
}

internal class ScheduleHttpApi(
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
        return ScheduleParser.parse(response.bodyAsText())
    }
}

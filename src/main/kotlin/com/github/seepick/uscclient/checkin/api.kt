package com.github.seepick.uscclient.checkin

import com.github.seepick.uscclient.PhpSessionId
import com.github.seepick.uscclient.ResponseStorage
import com.github.seepick.uscclient.UscConfig
import com.github.seepick.uscclient.safeGet
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import java.time.LocalDate

interface CheckinApi {
    suspend fun fetchPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage
}

class CheckinHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
    uscConfig: UscConfig,
) : CheckinApi {

    private val baseUrl = uscConfig.baseUrl

    override suspend fun fetchPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage {
        val response = http.safeGet(Url("$baseUrl/profile/check-ins")) {
            parameter("page", pageNr)
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Checkin-$pageNr")
        return CheckinsParser.parse(response.bodyAsText(), today)
    }
}

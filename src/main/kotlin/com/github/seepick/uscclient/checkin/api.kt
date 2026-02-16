package com.github.seepick.uscclient.checkin

import com.github.seepick.uscclient.UscConfig
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.safeGet
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import java.time.LocalDate

internal interface CheckinApi {
    suspend fun fetchPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage
}

internal class CheckinHttpApi(
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

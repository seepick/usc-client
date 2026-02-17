package com.github.seepick.uscclient.checkin

import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import java.time.LocalDate

internal interface CheckinApi {
    suspend fun fetchPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage
}

internal class CheckinHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
) : CheckinApi {
    private val log = logger {}
    override suspend fun fetchPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage {
        val response = http.safeGet("/profile/check-ins") {
            parameter("page", pageNr)
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Checkin-$pageNr")
        val rawHtml = response.bodyAsText()
        return try {
            CheckinsParser.parse(rawHtml, today)
        } catch (e: Exception) {
            log.warn { "Failed to parse USC response from /profile/check-ins!\n$rawHtml" }
            throw e
        }
    }
}

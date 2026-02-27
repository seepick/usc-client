package com.github.seepick.uscclient.thirdparty

import com.github.seepick.uscclient.shared.DateRange
import com.github.seepick.uscclient.shared.DateTimeRange
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.requireStatusOk
import com.github.seepick.uscclient.shared.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal enum class SessionType(val apiValue: String) {
    FITNESS("fitness"),
    COURSE_CLASS("course-class"),
//    RETREAT("retreat"),
//    SPECIAL_EVENT("special-event"),
//    SPECIAL_EVENT_NEW("special-event-new"),
    ;
}

internal class DnysFetcher(
    private val httpClient: HttpClient,
    private val responseStorage: ResponseStorage,
) {
    private val log = logger {}
    private val sessionTypes = listOf(SessionType.FITNESS, SessionType.COURSE_CLASS)
    private val dnysBaseUrl = "https://readonly-api.momence.com/host-plugins/host/39436/host-schedule/sessions"

    suspend fun fetchEvents(
        range: DateRange,
        pageSize: Int = 50,
    ): List<DnysEvent> {
        log.debug { "fetchEvents($range, pageSize=$pageSize)" }
        require(pageSize > 0) { "pageSize must be positive" }
        var currentPage = -1
        val responseJsons = mutableListOf<DnysJson>()
        do {
            currentPage++
            val response = fetchSinglePage(
                page = currentPage,
                pageSize = pageSize,
                range = range,
            )
            val responseJson = response.body<DnysJson>()
            responseJsons += responseJson
        } while (responseJson.pagination.totalCount > (1 + currentPage) * pageSize)
        return responseJsons.flatMap { it.payload }
            .filter { it.inPerson }
            .map { transform(it) }
    }

    private suspend fun fetchSinglePage(
        page: Int,
        pageSize: Int,
        range: DateRange,
    ): HttpResponse {
        val response =
            httpClient.safeGet(dnysBaseUrl) {
                sessionTypes.forEach {
                    parameter("sessionTypes[]", it.apiValue)
                }
                parameter("fromDate", dateFormatter.format(LocalDateTime.of(range.from, LocalTime.of(0, 0, 0))))
                parameter("toDate", dateFormatter.format(LocalDateTime.of(range.to, LocalTime.of(23, 59, 59))))
                parameter("pageSize", pageSize)
                parameter("page", page)
            }
        responseStorage.store(response, "DnysJsonPage-$page")
        response.requireStatusOk()
        return response
    }

    companion object {
        private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME
        fun transform(json: DnysPayloadJson) = DnysEvent(
            title = json.sessionName.trim(),
            teacher = json.teacher.replace("  ", " ").trim(),
            dateTimeRange = DateTimeRange(
                from = LocalDateTime.parse(json.startsAt, dateFormatter),
                to = LocalDateTime.parse(json.endsAt, dateFormatter),
            ),
        )
    }
}


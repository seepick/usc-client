package com.github.seepick.uscclient.thirdparty

import com.github.seepick.uscclient.readTestResponse
import com.github.seepick.uscclient.shared.DateRange
import com.github.seepick.uscclient.shared.DateTimeRange
import com.github.seepick.uscclient.shared.NoopResponseStorage
import com.github.seepick.uscclient.shared.jsonSerializer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.next
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import java.time.LocalDate

class DnysFetcherTest : StringSpec({
    "fetch" {
        DnysFetcher(
            httpClient = HttpClient(MockEngine { request ->
                request.url.toString() shouldBe "https://readonly-api.momence.com/host-plugins/host/39436/host-schedule/sessions?sessionTypes%5B%5D=fitness&sessionTypes%5B%5D=course-class&fromDate=2025-01-16T00%3A00%3A00&toDate=2025-01-16T23%3A59%3A59&pageSize=20&page=0"
                respond(
                    content = readTestResponse<String>("thirdparty/dnys_response.json"),
                    status = HttpStatusCode.OK,
                    headers = Headers.build {
                        append("Content-Type", "application/json")
                    })
            }) {
                install(ContentNegotiation) {
                    json(jsonSerializer)
                }
            }, NoopResponseStorage
        ).fetchEvents(
            pageSize = 20,
            range = DateRange(from = LocalDate.parse("2025-01-16"), to = LocalDate.parse("2025-01-16"))
        ).shouldHaveSize(7)
    }
    "transform" {
        DnysFetcher.transform(
            DnysPayloadJson(
                sessionName = "  title  ",
                startsAt = "2025-01-16T09:00:00",
                endsAt = "2025-01-16T10:00:00",
                teacher = "teacher  whitespace",
                inPerson = Arb.boolean().next(),
            )
        ) shouldBe DnysEvent(
            title = "title",
            teacher = "teacher whitespace",
            dateTimeRange = DateTimeRange(
                from = LocalDate.parse("2025-01-16").atTime(9, 0),
                to = LocalDate.parse("2025-01-16").atTime(10, 0),
            ),
        )
    }
})

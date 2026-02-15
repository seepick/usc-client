package seepick.localsportsclub.api

import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import seepick.localsportsclub.serializerLenient
import seepick.localsportsclub.toFlatMap

inline fun <reified T> buildMockClient(expectedUrl: String, phpSessionId: PhpSessionId, responsePayload: T) =
    HttpClient(MockEngine { request ->
        request.url.toString() shouldBe expectedUrl
        val headers = request.headers.toFlatMap()
        headers.shouldContain("x-requested-with" to "XMLHttpRequest")
        headers["Cookie"].shouldContain("PHPSESSID=$phpSessionId")
        respond(
            content = serializerLenient.encodeToString(responsePayload),
            status = HttpStatusCode.OK,
            headers = Headers.build {
                append("Content-Type", "application/json")
            })
    }) {
        install(ContentNegotiation) {
            json(serializerLenient)
        }
    }

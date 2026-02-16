package com.github.seepick.uscclient

import com.github.seepick.uscclient.utils.DateParser
import com.github.seepick.uscclient.utils.TimeRange
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.jsonSerializer
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
import io.ktor.util.StringValues
import io.ktor.util.toMap
import kotlinx.serialization.json.Json
import java.io.InputStream

internal inline fun <reified T> buildMockClient(expectedUrl: String, phpSessionId: PhpSessionId, responsePayload: T) =
    HttpClient(MockEngine { request ->
        request.url.toString() shouldBe expectedUrl
        val headers = request.headers.toFlatMap()
        headers.shouldContain("x-requested-with" to "XMLHttpRequest")
        headers["Cookie"].shouldContain("PHPSESSID=$phpSessionId")
        respond(
            content = jsonSerializer.encodeToString(responsePayload),
            status = HttpStatusCode.OK,
            headers = Headers.build {
                append("Content-Type", "application/json")
            })
    }) {
        install(ContentNegotiation) {
            json(jsonSerializer)
        }
    }

inline fun <reified T> readTestResponse(fileName: String, folder: String = "/response/"): T {
    val fileContent = readFromClasspath("$folder$fileName")
    return if (T::class == String::class) fileContent as T else jsonx.decodeFromString(fileContent)
}

val jsonx = Json {
    prettyPrint = true
    ignoreUnknownKeys = false
    isLenient = false
}

fun StringValues.toFlatMap(): Map<String, String> =
    toMap().mapValues { it.value.single() }

operator fun TimeRange.Companion.invoke(fromTo: String) =
    DateParser.parseTimes(fromTo, "-")

private object ResourceLocator

fun readFromClasspath(classpath: String): String =
    openFromClasspath(classpath).bufferedReader().use {
        it.readText()
    }

fun openFromClasspath(classpath: String): InputStream =
    ResourceLocator::class.java.getResourceAsStream(classpath)
        ?: throw Exception("Classpath resource not found at: [$classpath]")

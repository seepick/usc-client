package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import org.apache.http.ConnectionClosedException
import java.net.ConnectException
import java.net.SocketException

private val log = logger {}

val httpClient: HttpClient = HttpClient(Apache) {
    install(ContentNegotiation) {
        json(serializerLenient)
    }
//    install(createClientPlugin("CloudflareFix") {
//        on(Send) { request ->
//            request.headers.remove("Accept-Charset")
//            request.headers.remove("Accept")
//            request.headers["User-Agent"] = "xxx"
//            proceed(request)
//        }
//    })
    expectSuccess = false
}

val HttpResponse.phpSessionId: PhpSessionId
    get() = setCookie().singleOrNull { it.name == "PHPSESSID" }?.value?.let { PhpSessionId(it) }
        ?: error("PHPSESSID cookie is not set!")

suspend fun HttpResponse.requireStatusOk(message: suspend () -> String = { "" }) {
    if (status != HttpStatusCode.OK) {
        throw ApiException("Expected status 200 OK but was [$status] for: ${request.url}. ${message()}")
    }
}

// TODO rename
class ApiException(message: String, cause: Exception? = null) : Exception(message, cause)

suspend fun HttpClient.safeGet(url: Url, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
    safeRetry(HttpMethod.Get, url, block)

suspend fun HttpClient.safePost(url: Url, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
    safeRetry(HttpMethod.Post, url, block)

private suspend fun HttpClient.safeRetry(
    method: HttpMethod,
    url: Url,
    block: HttpRequestBuilder.() -> Unit = {},
): HttpResponse =
    retrySuspended(maxAttempts = 3, listOf(SocketException::class.java, ConnectionClosedException::class.java)) {
        safeAny(method, url, block)
    }

private suspend fun HttpClient.safeAny(
    method: HttpMethod,
    url: Url,
    block: HttpRequestBuilder.() -> Unit = {},
): HttpResponse {
    val response = try {
        request(url) {
            this.method = method
            block()
        }
    } catch (e: ConnectException) {
        log.error(e) { "Failed to ${method.value}: $url" }
        error("Failed to ${method.value}: $url")
    }
    log.debug { "Received response from: ${response.request.url}" }
    response.requireStatusOk {
        "Response body was: ${response.bodyAsText()}"
    }
    return response
}

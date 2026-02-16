package com.github.seepick.uscclient.shared

import com.github.seepick.uscclient.UscException
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
import io.ktor.serialization.kotlinx.json.json
import org.apache.http.ConnectionClosedException
import java.net.ConnectException
import java.net.SocketException
import kotlin.reflect.full.isSuperclassOf

private val log = logger {}

internal val httpClient: HttpClient = HttpClient(Apache) {
    install(ContentNegotiation) {
        json(jsonSerializer)
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

internal suspend fun HttpResponse.requireStatusOk(message: suspend () -> String = { "" }) {
    if (status != HttpStatusCode.OK) {
        throw UscException("Expected status 200 OK but was [$status] for: ${request.url}. ${message()}")
    }
}

internal suspend fun HttpClient.safeGet(url: Url, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
    safeRetry(HttpMethod.Get, url, block)

internal suspend fun HttpClient.safePost(url: Url, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
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

private suspend fun <T> retrySuspended(
    maxAttempts: Int,
    suppressExceptions: List<Class<out Exception>>,
    code: suspend () -> T,
): T =
    doRetrySuspend(maxAttempts = maxAttempts, suppressExceptions, currentAttempt = 1, code)

private suspend fun <T> doRetrySuspend(
    maxAttempts: Int,
    suppressExceptions: List<Class<out Exception>>,
    currentAttempt: Int,
    code: suspend () -> T,
): T =
    try {
        code()
    } catch (e: Exception) {
        if (suppressExceptions.any { it.kotlin.isSuperclassOf(e::class) } && currentAttempt < maxAttempts) {
            doRetrySuspend(maxAttempts = maxAttempts, suppressExceptions, currentAttempt = currentAttempt + 1, code)
        } else throw e
    }

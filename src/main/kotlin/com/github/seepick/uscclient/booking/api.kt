package com.github.seepick.uscclient.booking

import com.github.seepick.uscclient.UscConfig
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.jsonSerializer
import com.github.seepick.uscclient.shared.safePost
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal interface BookingApi {
    suspend fun book(session: PhpSessionId, activityOrFreetrainingId: Int): BookingResult
    suspend fun cancel(session: PhpSessionId, activityOrFreetrainingId: Int): CancelResult
}

internal class BookingHttpApi(
    private val http: HttpClient,
    uscConfig: UscConfig,
    private val responseStorage: ResponseStorage,
) : BookingApi {

    private val log = logger {}
    private val baseUrl = uscConfig.baseUrl

    override suspend fun book(session: PhpSessionId, activityOrFreetrainingId: Int): BookingResult {
        log.info { "About to book activityOrFreetrainingId: $activityOrFreetrainingId" }
        val response = http.safePost(Url("$baseUrl/search/book/$activityOrFreetrainingId")) {
            cookie("PHPSESSID", session.value)
            header("x-requested-with", "XMLHttpRequest")
        }
        responseStorage.store(response, "Booking-$activityOrFreetrainingId")
        return handleBookResponse(response.bodyAsText(), activityOrFreetrainingId)
    }

    private fun handleBookResponse(responseText: String, activityOrFreetrainingId: Int): BookingResult {
        val jsonElement = jsonSerializer.parseToJsonElement(responseText)
        val isSuccessElement = jsonElement.jsonObject["success"] ?: error("Invalid response json: $responseText")
        return if (isSuccessElement.jsonPrimitive.boolean) {
            val successResponse = jsonSerializer.decodeFromString<BookingSuccessResponseJson>(responseText)
            require(successResponse.success)
            require(successResponse.data.state == "booked" || successResponse.data.state == "scheduled")
            BookingResult.BookingSuccess
        } else {
            val errorResponse = jsonSerializer.decodeFromString<BookingErrorResponseJson>(responseText)
            require(!errorResponse.success)
            require(errorResponse.data.state == "error")
            log.warn { "Received error response from API while booking activity/freetraining $activityOrFreetrainingId: $errorResponse" }
            return BookingResult.BookingFail(message = errorResponse.data.alert)
        }
    }

    override suspend fun cancel(session: PhpSessionId, activityOrFreetrainingId: Int): CancelResult {
        log.info { "About to cancel booking for activityOrFreetrainingId: $activityOrFreetrainingId" }
        val response = http.safePost(Url("$baseUrl/search/cancel/$activityOrFreetrainingId")) {
            cookie("PHPSESSID", session.value)
            header("x-requested-with", "XMLHttpRequest")
        }
        responseStorage.store(response, "Cancel-$activityOrFreetrainingId")
        return handleCancelResponse(response.bodyAsText(), activityOrFreetrainingId)
    }

    private fun handleCancelResponse(responseText: String, activityOrFreetrainingId: Int): CancelResult {
        val jsonElement = jsonSerializer.parseToJsonElement(responseText)
        val isSuccessElement = jsonElement.jsonObject["success"] ?: error("Invalid response json: $responseText")
        return if (isSuccessElement.jsonPrimitive.boolean) {
            val successResponse = jsonSerializer.decodeFromString<CancellationSuccessResponseJson>(responseText)
            require(successResponse.success)
            require(successResponse.data.state == "cancel_customer")
            CancelResult.CancelSuccess
        } else {
            val errorResponse = jsonSerializer.decodeFromString<CancellationErrorResponseJson>(responseText)
            require(!errorResponse.success)
            require(errorResponse.data.state == "error")
            log.warn { "Received error response from API while booking activity/freetraining $activityOrFreetrainingId: $errorResponse" }
            CancelResult.CancelFail(errorResponse.data.alert)
        }
    }
}

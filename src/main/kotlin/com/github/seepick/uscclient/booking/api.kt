package seepick.localsportsclub.api.booking

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import seepick.localsportsclub.api.PhpSessionId
import seepick.localsportsclub.api.ResponseStorage
import seepick.localsportsclub.api.UscConfig
import seepick.localsportsclub.serializerLenient
import seepick.localsportsclub.service.safePost

interface BookingApi {
    suspend fun book(session: PhpSessionId, activityOrFreetrainingId: Int): BookingResult
    suspend fun cancel(session: PhpSessionId, activityOrFreetrainingId: Int): CancelResult
}

class BookingHttpApi(
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
        val jsonElement = serializerLenient.parseToJsonElement(responseText)
        val isSuccessElement = jsonElement.jsonObject["success"] ?: error("Invalid response json: $responseText")
        return if (isSuccessElement.jsonPrimitive.boolean) {
            val successResponse = serializerLenient.decodeFromString<BookingSuccessResponseJson>(responseText)
            require(successResponse.success)
            require(successResponse.data.state == "booked" || successResponse.data.state == "scheduled")
            BookingResult.BookingSuccess
        } else {
            val errorResponse = serializerLenient.decodeFromString<BookingErrorResponseJson>(responseText)
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
        val jsonElement = serializerLenient.parseToJsonElement(responseText)
        val isSuccessElement = jsonElement.jsonObject["success"] ?: error("Invalid response json: $responseText")
        return if (isSuccessElement.jsonPrimitive.boolean) {
            val successResponse = serializerLenient.decodeFromString<CancellationSuccessResponseJson>(responseText)
            require(successResponse.success)
            require(successResponse.data.state == "cancel_customer")
            CancelResult.CancelSuccess
        } else {
            val errorResponse = serializerLenient.decodeFromString<CancellationErrorResponseJson>(responseText)
            require(!errorResponse.success)
            require(errorResponse.data.state == "error")
            log.warn { "Received error response from API while booking activity/freetraining $activityOrFreetrainingId: $errorResponse" }
            CancelResult.CancelFail(errorResponse.data.alert)
        }
    }
}

sealed interface BookingResult {
    data object BookingSuccess : BookingResult
    data class BookingFail(val message: String) : BookingResult
}

sealed interface CancelResult {
    data object CancelSuccess : CancelResult
    data class CancelFail(val message: String) : CancelResult
}

@Serializable
data class BookingSuccessResponseJson(
    val success: Boolean,
    val data: BookingSuccessDataJson,
)

@Serializable
data class BookingSuccessDataJson(
    val id: Int,
    val state: String, // "booked" == activity, "scheduled" == freetraining
    // label, alert, isManual, cancelButton
    val freeSpots: FreeSpotsJson,
)

@Serializable
data class FreeSpotsJson(
    val current: Int,
    val maximum: Int,
)

@Serializable
data class BookingErrorResponseJson(
    val success: Boolean,
    val data: BookingErrorDataJson,
)

@Serializable
data class BookingErrorDataJson(
    val state: String, // "error"
    val alert: String,
)

@Serializable
data class CancellationSuccessResponseJson(
    val success: Boolean,
    val data: CancellationSuccessDataJson,
)

@Serializable
data class CancellationSuccessDataJson(
    val id: Int,
    val state: String, // "cancel_customer"
    // label, alert
    val freeSpots: FreeSpotsJson,
)

@Serializable
data class CancellationErrorResponseJson(
    val success: Boolean,
    val data: CancellationErrorDataJson,
)

@Serializable
data class CancellationErrorDataJson(
    val state: String, // "error"
    val alert: String, // error detail message
)

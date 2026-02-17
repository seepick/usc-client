package com.github.seepick.uscclient.booking

import com.github.seepick.uscclient.buildMockClient
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.NoopResponseStorage
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient

class BookingHttpApiTest : DescribeSpec() {

    private val activityId = 42
    private val phpSessionId = PhpSessionId("testPhpSessionId")
    private val anyFreeSpotsJson = FreeSpotsJson(21, 42)
    private val errorMessage = "some error message routed through API"

    private fun api(httpClient: HttpClient): BookingApi = BookingHttpApi(
        http = httpClient,
        responseStorage = NoopResponseStorage,
    )

    init {
        describe("When book") {
            it("Given successful response Then return booking success") {
                val rootJson = BookingSuccessResponseJson(
                    success = true,
                    data = BookingSuccessDataJson(
                        id = activityId,
                        state = "booked",
                        freeSpots = anyFreeSpotsJson,
                    ),
                )
                val response = api(
                    buildMockClient(
                        expectedUrl = "/search/book/$activityId",
                        phpSessionId = phpSessionId,
                        responsePayload = rootJson
                    )
                ).book(phpSessionId, activityId)

                response shouldBe BookingResult.BookingSuccess
            }
            it("Given error response Then return booking failure with message") {
                val rootJson = BookingErrorResponseJson(
                    success = false,
                    data = BookingErrorDataJson(
                        state = "error",
                        alert = errorMessage,
                    )
                )
                val response = api(
                    buildMockClient(
                        expectedUrl = "/search/book/$activityId",
                        phpSessionId = phpSessionId,
                        responsePayload = rootJson
                    )
                ).book(phpSessionId, activityId)

                response shouldBe BookingResult.BookingFail(errorMessage)
            }
        }
        describe("When cancel") {
            it("Given successful response Then return cancel success") {
                val rootJson = CancellationSuccessResponseJson(
                    success = true,
                    data = CancellationSuccessDataJson(
                        id = activityId,
                        state = "cancel_customer",
                        freeSpots = anyFreeSpotsJson,
                    ),
                )
                val response = api(
                    buildMockClient(
                        expectedUrl = "/search/cancel/$activityId",
                        phpSessionId = phpSessionId,
                        responsePayload = rootJson
                    )
                ).cancel(phpSessionId, activityId)

                response shouldBe CancelResult.CancelSuccess
            }
            it("Given error response Then return cancel failure with message") {
                val rootJson = CancellationErrorResponseJson(
                    success = false,
                    data = CancellationErrorDataJson(
                        state = "error",
                        alert = errorMessage,
                    )
                )
                val response = api(
                    buildMockClient(
                        expectedUrl = "/search/cancel/$activityId",
                        phpSessionId = phpSessionId,
                        responsePayload = rootJson
                    )
                ).cancel(phpSessionId, activityId)

                response shouldBe CancelResult.CancelFail(errorMessage)
            }
        }
    }
}

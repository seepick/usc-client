package seepick.localsportsclub.api.booking

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.ktor.client.HttpClient
import seepick.localsportsclub.api.NoopResponseStorage
import seepick.localsportsclub.api.PhpSessionId
import seepick.localsportsclub.api.buildMockClient
import seepick.localsportsclub.uscConfig

class BookingHttpApiTest : DescribeSpec() {

    private val activityId = 42
    private val uscConfig = Arb.uscConfig().next()
    private val phpSessionId = PhpSessionId("testPhpSessionId")
    private val anyFreeSpotsJson = FreeSpotsJson(21, 42)
    private val errorMessage = "some error message routed through API"

    private fun api(httpClient: HttpClient): BookingApi = BookingHttpApi(
        http = httpClient,
        uscConfig = uscConfig,
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
                        expectedUrl = "${uscConfig.baseUrl}/search/book/$activityId",
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
                        expectedUrl = "${uscConfig.baseUrl}/search/book/$activityId",
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
                        expectedUrl = "${uscConfig.baseUrl}/search/cancel/$activityId",
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
                        expectedUrl = "${uscConfig.baseUrl}/search/cancel/$activityId",
                        phpSessionId = phpSessionId,
                        responsePayload = rootJson
                    )
                ).cancel(phpSessionId, activityId)

                response shouldBe CancelResult.CancelFail(errorMessage)
            }
        }
    }
}

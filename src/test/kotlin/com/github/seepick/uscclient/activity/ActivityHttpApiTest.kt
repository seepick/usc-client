package seepick.localsportsclub.api.activity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import seepick.localsportsclub.StaticClock
import seepick.localsportsclub.api.NoopResponseStorage
import seepick.localsportsclub.api.PhpSessionId
import seepick.localsportsclub.api.StatsDistrictJson
import seepick.localsportsclub.api.StatsJson
import seepick.localsportsclub.api.buildMockClient
import seepick.localsportsclub.service.model.City
import seepick.localsportsclub.service.model.Plan
import seepick.localsportsclub.uscConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivityHttpApiTest : StringSpec() {
    private val uscConfig = Arb.uscConfig().next()
    private val phpSessionId = PhpSessionId("testPhpSessionId")
    private val clock = StaticClock()
    private val filter = ActivitiesFilter(
        city = City.Amsterdam,
        plan = Plan.UscPlan.Medium,
        date = LocalDate.of(2024, 12, 1),
    )

    init {
        "Given data returned When fetch page Then return data" {
            val rootJson = buildActivitiesJson(success = true, showMore = false)
            val expectedUrl =
                "${uscConfig.baseUrl}/activities?city_id=${filter.city.id}&date=${filter.date.format(DateTimeFormatter.ISO_LOCAL_DATE)}&plan_type=${filter.plan.id}&type%5B%5D=${ActivityType.OnSite.apiValue}&service_type=${ServiceType.Courses.apiValue}&page=1"
            val http =
                buildMockClient(expectedUrl = expectedUrl, phpSessionId = phpSessionId, responsePayload = rootJson)
            val api = ActivityHttpApi(
                http = http,
                uscConfig = uscConfig,
                clock = clock,
                responseStorage = NoopResponseStorage,
                pageSizeHint = 1,
            )

            val response = api.fetchPages(phpSessionId, filter, ServiceType.Courses)

            response.shouldBeSingleton().first() shouldBe rootJson.data
        }
    }
}


private fun buildActivitiesJson(success: Boolean, showMore: Boolean) = ActivitiesJson(
    success = success, data = ActivitiesDataJson(
        showMore = showMore, content = "HTML", stats = StatsJson(
            category = listOf(),
            district = StatsDistrictJson(district = listOf(), areas = listOf()),
            venue = listOf()
        ), emptySnippet = null, searchExecutedEvent = "{}", regionSelectorSelected = null
    )
)

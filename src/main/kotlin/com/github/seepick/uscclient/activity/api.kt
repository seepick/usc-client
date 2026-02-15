package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.ApiException
import com.github.seepick.uscclient.City
import com.github.seepick.uscclient.DateTimeRange
import com.github.seepick.uscclient.PhpSessionId
import com.github.seepick.uscclient.Plan
import com.github.seepick.uscclient.ResponseStorage
import com.github.seepick.uscclient.UscConfig
import com.github.seepick.uscclient.fetchPageable
import com.github.seepick.uscclient.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.cookie
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface ActivityApi {

    suspend fun fetchPages(
        session: PhpSessionId,
        filter: ActivitiesFilter,
        serviceType: ServiceType,
    ): List<ActivitiesDataJson>

    suspend fun fetchActivityDetails(session: PhpSessionId, id: Int): ActivityDetails
    suspend fun fetchFreetrainingDetails(session: PhpSessionId, id: Int): FreetrainingDetails
}

data class ActivitiesFilter(
    val city: City,
    val plan: Plan,
    val date: LocalDate,
)

enum class ActivityType(val apiValue: String) {
    OnSite("onsite"),
//    OnlineLive("live"),
}

enum class ServiceType(val apiValue: Int) {
    Courses(0),
    FreeTraining(1),
}

data class ActivityDetails(
    val name: String,
    val dateTimeRange: DateTimeRange,
    val venueName: String,
    val category: String,
    val spotsLeft: Int,
    val cancellationDateLimit: LocalDateTime?,
    val plan: Plan.UscPlan,
    val teacher: String?,
    val description: String,
)

data class FreetrainingDetails(
    val id: Int,
    val name: String,
    val date: LocalDate,
    val venueSlug: String,
    val category: String,
    val plan: Plan.UscPlan,
)

class ActivityHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
    uscConfig: UscConfig,
    private val currentYear: Int,
    private val pageSizeHint: Int = 100,
) : ActivityApi {

    private val log = logger {}
    private val baseUrl = uscConfig.baseUrl

    override suspend fun fetchPages(
        session: PhpSessionId,
        filter: ActivitiesFilter,
        serviceType: ServiceType,
    ): List<ActivitiesDataJson> =
        fetchPageable(pageSizeHint) { fetchPage(session, filter, serviceType, it) }

    // /activities?service_type=0&city=1144&date=2024-12-16&business_type[]=b2c&plan_type=3&type[]=onsite&page=2
    private suspend fun fetchPage(
        session: PhpSessionId,
        filter: ActivitiesFilter,
        serviceType: ServiceType,
        page: Int,
    ): ActivitiesDataJson {
        val response = http.safeGet(Url("$baseUrl/activities")) {
            cookie("PHPSESSID", session.value)
            header("x-requested-with", "XMLHttpRequest") // IMPORTANT! to change the response to JSON!!!
            parameter("city_id", filter.city.id)
            parameter("date", filter.date.format(DateTimeFormatter.ISO_LOCAL_DATE)) // 2024-12-16
            parameter("plan_type", filter.plan.id)
//            parameter("business_type[]", "b2c")
            parameter("type[]", ActivityType.OnSite.apiValue) // onsite or online
            parameter("service_type", serviceType.apiValue) // (scheduled) courses or free training (dropin)
            parameter("page", page)
        }
        responseStorage.store(response, "ActivtiesPage-$page")
        val json = response.body<ActivitiesJson>()
        if (!json.success) {
            throw ApiException("Activities endpoint returned failure!")
        }
        return json.data
    }

    override suspend fun fetchActivityDetails(session: PhpSessionId, id: Int): ActivityDetails {
        log.debug { "Fetching details for $id" }
        val response = http.safeGet(Url("$baseUrl/class-details/$id")) {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "ActivtiesDetails-$id")
        return ActivityDetailsParser.parseDetails(response.bodyAsText(), currentYear)
    }

    override suspend fun fetchFreetrainingDetails(session: PhpSessionId, id: Int): FreetrainingDetails {
        val response = http.safeGet(Url("$baseUrl/class-details/$id")) {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "FreetrainingDetails-$id")
        return ActivityDetailsParser.parseFreetraining(response.bodyAsText(), currentYear)
    }
}

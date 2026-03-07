package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.UscErrorReason
import com.github.seepick.uscclient.UscException
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.fetchPageable
import com.github.seepick.uscclient.shared.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.cookie
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal interface ActivityApi {
    suspend fun fetchPages(
        session: PhpSessionId,
        filter: ActivitiesFilter,
        serviceType: ServiceType,
    ): List<ActivitiesDataJson>

    suspend fun fetchActivityDetails(
        session: PhpSessionId,
        id: Int,
    ): ActivityDetails

    suspend fun fetchFreetrainingDetails(
        session: PhpSessionId,
        id: Int,
    ): FreetrainingDetails
}

internal enum class ActivityType(val apiValue: String) {
    OnSite("onsite"),
    OnlineLive("live"),
}

/** Similar to [com.github.seepick.uscclient.schedule.ScheduleEntityType]. */
internal enum class ServiceType(val apiValue: Int) {
    /** scheduled courses */
    Courses(0),
    /** dropin free training */
    FreeTraining(1),
}

internal class ActivityHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
    private val currentYear: Int,
    private val pageSizeHint: Int = 100,
) : ActivityApi {

    private val log = logger {}

    override suspend fun fetchPages(
        session: PhpSessionId,
        filter: ActivitiesFilter,
        serviceType: ServiceType,
    ): List<ActivitiesDataJson> =
        fetchPageable(pageSizeHint) { page ->
            fetchPage(
                session,
                ActivitiesQueryParams(
                    city = filter.city,
                    plan = filter.plan,
                    date = filter.date,
                    serviceType = serviceType,
                    page = page,
                )
            )
        }

    // /activities?service_type=0&city=1144&date=2024-12-16&business_type[]=b2c&plan_type=3&type[]=onsite&page=2
    private suspend fun fetchPage(
        session: PhpSessionId,
        params: ActivitiesQueryParams,
    ): ActivitiesDataJson {
        log.debug { "fetchPage($params)" }
        val response = http.safeGet("/activities") {
            cookie("PHPSESSID", session.value)
            header("x-requested-with", "XMLHttpRequest") // IMPORTANT! to change the response to JSON!!!
            parameter("city_id", params.city.id)
            parameter("date", params.date.format(DateTimeFormatter.ISO_LOCAL_DATE)) // 2024-12-16
            parameter("plan_type", params.plan.id)
            parameter("type[]", ActivityType.OnSite.apiValue) // onsite or online
            parameter("service_type", params.serviceType.apiValue) // (scheduled) courses or free training (dropin)
            parameter("page", params.page)
        }
        responseStorage.store(response, "ActivtiesPage-${params.page}")
        val json = response.body<ActivitiesJson>()
        if (!json.success) {
            throw UscException("Activities endpoint returned failure!", UscErrorReason.JsonSuccessFalse)
        }
        return json.data
    }

    override suspend fun fetchActivityDetails(session: PhpSessionId, id: Int): ActivityDetails {
        log.debug { "Fetching details for $id" }
        val response = http.safeGet("/class-details/$id") {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "ActivtiesDetails-$id")
        return ActivityDetailsParser.parseDetails(response.bodyAsText(), currentYear)
    }

    override suspend fun fetchFreetrainingDetails(session: PhpSessionId, id: Int): FreetrainingDetails {
        val response = http.safeGet("/class-details/$id") {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "FreetrainingDetails-$id")
        return ActivityDetailsParser.parseFreetraining(response.bodyAsText(), currentYear)
    }
}

internal data class ActivitiesQueryParams(
    val city: City,
    val plan: Plan,
    val date: LocalDate,
    // parameter("business_type[]", "b2c")
    val serviceType: ServiceType, //
    val page: Int,
)

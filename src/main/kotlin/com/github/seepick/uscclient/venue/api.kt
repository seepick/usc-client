package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.UscException
import com.github.seepick.uscclient.login.PhpSessionId
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
import java.util.concurrent.atomic.AtomicInteger

internal interface VenueApi {
    suspend fun fetchPages(session: PhpSessionId, filter: VenuesFilter): List<VenuesDataJson>
    suspend fun fetchDetails(session: PhpSessionId, slug: String): VenueDetails
}

internal class VenueHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
// FIXME   private val progress: SyncProgress,
) : VenueApi {

    private val log = logger {}
    private var pageCounter = AtomicInteger(-1)

    override suspend fun fetchPages(session: PhpSessionId, filter: VenuesFilter): List<VenuesDataJson> {
        pageCounter.set(0)
        return fetchPageable(20) { fetchPage(session, filter, it) }
    }

    // GET https://urbansportsclub.com/nl/venues?city_id=1144&plan_type=3&page=2
    private suspend fun fetchPage(session: PhpSessionId, filter: VenuesFilter, page: Int): VenuesDataJson {
        log.debug { "Fetching venue page $page" }
//        progress.onProgressVenues("Page ${pageCounter.incrementAndGet()}")
        val response = http.safeGet("/venues") {
            cookie("PHPSESSID", session.value)
            header("x-requested-with", "XMLHttpRequest") // IMPORTANT! to change the response to JSON!!!
            parameter("city_id", filter.city.id)
            parameter("plan_type", filter.plan.id)
            parameter("page", page)
        }
        responseStorage.store(response, "VenuesPage-$page")
        val json = response.body<VenuesJson>()
        if (!json.success) {
            throw UscException("Venues endpoint returned failure!")
        }
        return json.data
    }

    override suspend fun fetchDetails(session: PhpSessionId, slug: String): VenueDetails {
        log.debug { "Fetching details for: [$slug]" }
        val venueUrl = "/venues/$slug"
        val response = http.safeGet(venueUrl) {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "VenueDetails-$slug")
        try {
            return VenueDetailsParser.parse(response.bodyAsText()).also {
                log.debug { "Fetched details for venue '$slug': $it" }
            }
        } catch (e: Exception) {
            log.error { "Unable to parse response for venue: $venueUrl" }
            throw e
        }
    }
}

//private fun SyncProgress.onProgressVenues(detail: String?) {
//    onProgress("Venues", detail)
//}

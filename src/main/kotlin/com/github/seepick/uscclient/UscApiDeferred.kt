package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivitiesFilter
import com.github.seepick.uscclient.shared.PageProgressListener
import com.github.seepick.uscclient.venue.VenuesFilter
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

public class UscApiDeferred(
    private val configProvider: () -> UscConfig,
    private val onConnected: (UscApi) -> Unit = {},
) : UscApi {

    private val log = logger {}

    private val delegate by lazy {
        runBlocking {
            log.info { "Establishing deferred connection." }
            UscConnector().connect(configProvider()).also { api ->
                onConnected(api)
            }
        }
    }

    override suspend fun fetchVenues(filter: VenuesFilter, listener: PageProgressListener) =
        delegate.fetchVenues(filter)

    override suspend fun fetchVenueDetail(slug: String) = delegate.fetchVenueDetail(slug)

    override suspend fun fetchActivities(filter: ActivitiesFilter) = delegate.fetchActivities(filter)

    override suspend fun fetchActivityDetails(activityId: Int) = delegate.fetchActivityDetails(activityId)

    override suspend fun fetchFreetrainings(filter: ActivitiesFilter) = delegate.fetchFreetrainings(filter)

    override suspend fun fetchFreetrainingDetails(freetrainingId: Int) =
        delegate.fetchFreetrainingDetails(freetrainingId)

    override suspend fun fetchScheduleds() = delegate.fetchScheduleds()

    override suspend fun fetchCheckinsPage(pageNr: Int, today: LocalDate) = delegate.fetchCheckinsPage(pageNr, today)

    override suspend fun fetchMembership() = delegate.fetchMembership()

    override suspend fun book(activityOrFreetrainingId: Int) = delegate.book(activityOrFreetrainingId)

    override suspend fun cancel(activityOrFreetrainingId: Int) = delegate.cancel(activityOrFreetrainingId)
}

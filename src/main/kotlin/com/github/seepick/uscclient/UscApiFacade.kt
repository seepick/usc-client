package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivitiesFilter
import com.github.seepick.uscclient.activity.ActivitiesParser
import com.github.seepick.uscclient.activity.ActivityDetails
import com.github.seepick.uscclient.activity.ActivityHttpApi
import com.github.seepick.uscclient.activity.FreetrainingDetails
import com.github.seepick.uscclient.activity.FreetrainingInfo
import com.github.seepick.uscclient.activity.ServiceType
import com.github.seepick.uscclient.booking.BookingHttpApi
import com.github.seepick.uscclient.booking.BookingResult
import com.github.seepick.uscclient.booking.CancelResult
import com.github.seepick.uscclient.checkin.CheckinHttpApi
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.plan.Membership
import com.github.seepick.uscclient.plan.MembershipHttpApi
import com.github.seepick.uscclient.schedule.ScheduleHttpApi
import com.github.seepick.uscclient.shared.NoopResponseStorage
import com.github.seepick.uscclient.shared.ResponseStorageImpl
import com.github.seepick.uscclient.venue.VenueHttpApi
import com.github.seepick.uscclient.venue.VenueParser
import com.github.seepick.uscclient.venue.VenuesFilter
import io.ktor.client.HttpClient
import java.io.File
import java.time.LocalDate

internal class UscApiFacade(
    val phpSessionId: PhpSessionId,
    httpClient: HttpClient,
    responseLogFolder: File?,
    currentYear: Int,
) : UscApi {

    private val responseStorage =
        if (responseLogFolder != null) ResponseStorageImpl(responseLogFolder)
        else NoopResponseStorage

    val venueApi = VenueHttpApi(httpClient, responseStorage)
    val activityApi = ActivityHttpApi(httpClient, responseStorage, currentYear)
    val scheduleApi = ScheduleHttpApi(httpClient, responseStorage)
    val checkinApi = CheckinHttpApi(httpClient, responseStorage)
    val bookingApi = BookingHttpApi(httpClient, responseStorage)
    val membershipApi = MembershipHttpApi(httpClient, responseStorage)

    override suspend fun fetchVenues(filter: VenuesFilter) =
        venueApi.fetchPages(phpSessionId, filter).flatMap {
            VenueParser.parseHtmlContent(it.content)
        }

    override suspend fun fetchVenueDetail(slug: String) =
        venueApi.fetchDetails(phpSessionId, slug)

    override suspend fun fetchActivities(filter: ActivitiesFilter) =
        activityApi.fetchPages(phpSessionId, filter, ServiceType.Courses).flatMap {
            ActivitiesParser.parseContent(it.content, filter.date)
        }

    override suspend fun fetchActivityDetails(activityId: Int): ActivityDetails =
        activityApi.fetchActivityDetails(phpSessionId, activityId)

    override suspend fun fetchFreetrainings(filter: ActivitiesFilter): List<FreetrainingInfo> =
        activityApi.fetchPages(phpSessionId, filter, ServiceType.FreeTraining).flatMap {
            ActivitiesParser.parseFreetrainingContent(it.content)
        }

    override suspend fun fetchFreetrainingDetails(freetrainingId: Int): FreetrainingDetails =
        activityApi.fetchFreetrainingDetails(phpSessionId, freetrainingId)

    override suspend fun fetchScheduleds() =
        scheduleApi.fetchScheduleds(phpSessionId)

    override suspend fun fetchCheckinsPage(pageNr: Int, today: LocalDate) =
        checkinApi.fetchPage(phpSessionId, pageNr, today)

    override suspend fun book(activityOrFreetrainingId: Int): BookingResult =
        bookingApi.book(phpSessionId, activityOrFreetrainingId)

    override suspend fun cancel(activityOrFreetrainingId: Int): CancelResult =
        bookingApi.cancel(phpSessionId, activityOrFreetrainingId)

    override suspend fun fetchMembership(): Membership =
        membershipApi.fetch(phpSessionId)
}

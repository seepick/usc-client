package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivitiesFilter
import com.github.seepick.uscclient.activity.ActivitiesParser
import com.github.seepick.uscclient.activity.ActivityApi
import com.github.seepick.uscclient.activity.ActivityDetails
import com.github.seepick.uscclient.activity.FreetrainingDetails
import com.github.seepick.uscclient.activity.FreetrainingInfo
import com.github.seepick.uscclient.activity.ServiceType
import com.github.seepick.uscclient.booking.BookingApi
import com.github.seepick.uscclient.booking.BookingResult
import com.github.seepick.uscclient.booking.CancelResult
import com.github.seepick.uscclient.checkin.CheckinApi
import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.login.LoginApi
import com.github.seepick.uscclient.login.LoginResult
import com.github.seepick.uscclient.plan.Membership
import com.github.seepick.uscclient.plan.MembershipApi
import com.github.seepick.uscclient.schedule.ScheduleApi
import com.github.seepick.uscclient.venue.VenueApi
import com.github.seepick.uscclient.venue.VenueParser
import com.github.seepick.uscclient.venue.VenuesFilter
import com.github.seepick.uscclient.login.PhpSessionId
import java.time.LocalDate

internal class UscApiFacade(
    private val loginApi: LoginApi,
    private val venueApi: VenueApi,
    private val activityApi: ActivityApi,
    private val scheduleApi: ScheduleApi,
    private val checkinApi: CheckinApi,
    private val bookingApi: BookingApi,
    private val membershipApi: MembershipApi,
) : UscApi {

    override suspend fun login(credentials: Credentials): LoginResult =
        loginApi.login(credentials)

    override suspend fun fetchVenues(session: PhpSessionId, filter: VenuesFilter) =
        venueApi.fetchPages(session, filter).flatMap {
            VenueParser.parseHtmlContent(it.content)
        }

    override suspend fun fetchVenueDetail(session: PhpSessionId, slug: String) =
        venueApi.fetchDetails(session, slug)

    override suspend fun fetchActivities(session: PhpSessionId, filter: ActivitiesFilter) =
        activityApi.fetchPages(session, filter, ServiceType.Courses).flatMap {
            ActivitiesParser.parseContent(it.content, filter.date)
        }

    override suspend fun fetchActivityDetails(session: PhpSessionId, activityId: Int): ActivityDetails =
        activityApi.fetchActivityDetails(session, activityId)

    override suspend fun fetchFreetrainings(session: PhpSessionId, filter: ActivitiesFilter): List<FreetrainingInfo> =
        activityApi.fetchPages(session, filter, ServiceType.FreeTraining).flatMap {
            ActivitiesParser.parseFreetrainingContent(it.content)
        }

    override suspend fun fetchFreetrainingDetails(session: PhpSessionId, freetrainingId: Int): FreetrainingDetails =
        activityApi.fetchFreetrainingDetails(session, freetrainingId)

    override suspend fun fetchScheduleRows(session: PhpSessionId) =
        scheduleApi.fetchScheduleRows(session)

    override suspend fun fetchCheckinsPage(session: PhpSessionId, pageNr: Int, today: LocalDate) =
        checkinApi.fetchPage(session, pageNr, today)

    override suspend fun book(session: PhpSessionId, activityOrFreetrainingId: Int): BookingResult =
        bookingApi.book(session, activityOrFreetrainingId)

    override suspend fun cancel(session: PhpSessionId, activityOrFreetrainingId: Int): CancelResult =
        bookingApi.cancel(session, activityOrFreetrainingId)

    override suspend fun fetchMembership(session: PhpSessionId): Membership =
        membershipApi.fetch(session)
}

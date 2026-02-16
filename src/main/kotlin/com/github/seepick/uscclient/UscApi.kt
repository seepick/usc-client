package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivitiesFilter
import com.github.seepick.uscclient.activity.ActivityDetails
import com.github.seepick.uscclient.activity.ActivityInfo
import com.github.seepick.uscclient.activity.FreetrainingDetails
import com.github.seepick.uscclient.activity.FreetrainingInfo
import com.github.seepick.uscclient.booking.BookingResult
import com.github.seepick.uscclient.booking.CancelResult
import com.github.seepick.uscclient.checkin.CheckinsPage
import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.login.LoginResult
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.plan.Membership
import com.github.seepick.uscclient.schedule.ScheduleRow
import com.github.seepick.uscclient.venue.VenueDetails
import com.github.seepick.uscclient.venue.VenueInfo
import com.github.seepick.uscclient.venue.VenuesFilter
import java.time.LocalDate

/**
 * Access instance by using the koin module function [uscClientModule].
 */
public interface UscApi {
    suspend fun login(credentials: Credentials): LoginResult
    suspend fun fetchVenues(session: PhpSessionId, filter: VenuesFilter): List<VenueInfo>
    suspend fun fetchVenueDetail(session: PhpSessionId, slug: String): VenueDetails
    suspend fun fetchActivities(session: PhpSessionId, filter: ActivitiesFilter): List<ActivityInfo>
    suspend fun fetchActivityDetails(session: PhpSessionId, activityId: Int): ActivityDetails
    suspend fun fetchFreetrainings(session: PhpSessionId, filter: ActivitiesFilter): List<FreetrainingInfo>
    suspend fun fetchFreetrainingDetails(session: PhpSessionId, freetrainingId: Int): FreetrainingDetails
    suspend fun fetchScheduleRows(session: PhpSessionId): List<ScheduleRow>
    suspend fun fetchCheckinsPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage
    suspend fun book(session: PhpSessionId, activityOrFreetrainingId: Int): BookingResult
    suspend fun cancel(session: PhpSessionId, activityOrFreetrainingId: Int): CancelResult
    suspend fun fetchMembership(session: PhpSessionId): Membership
}

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
import com.github.seepick.uscclient.model.UscLang
import com.github.seepick.uscclient.plan.Membership
import com.github.seepick.uscclient.schedule.BookedOrScheduled
import com.github.seepick.uscclient.venue.VenueDetails
import com.github.seepick.uscclient.venue.VenueInfo
import com.github.seepick.uscclient.venue.VenuesFilter
import java.time.LocalDate

public interface UscApi {
    suspend fun fetchVenues(filter: VenuesFilter): List<VenueInfo>
    suspend fun fetchVenueDetail(slug: String): VenueDetails
    suspend fun fetchActivities(filter: ActivitiesFilter): List<ActivityInfo>
    suspend fun fetchActivityDetails(activityId: Int): ActivityDetails
    suspend fun fetchFreetrainings(filter: ActivitiesFilter): List<FreetrainingInfo>
    suspend fun fetchFreetrainingDetails(freetrainingId: Int): FreetrainingDetails
    suspend fun fetchScheduleds(): List<BookedOrScheduled>
    suspend fun fetchCheckinsPage(pageNr: Int, today: LocalDate): CheckinsPage
    suspend fun fetchMembership(): Membership
    suspend fun book(activityOrFreetrainingId: Int): BookingResult
    suspend fun cancel(activityOrFreetrainingId: Int): CancelResult

    companion object // extensions
}

public interface UscConnector {

    suspend fun connect(config: UscConfig): UscApi
    suspend fun verifyConnection(
        credentials: Credentials,
        lang: UscLang = UscLang.English,
    ): ConnectionVerificationResult

    companion object {
        operator fun invoke(): UscConnector = UscConnectorImpl()
    }
}

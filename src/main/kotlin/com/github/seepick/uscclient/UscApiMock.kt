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
import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.model.Country
import com.github.seepick.uscclient.plan.Membership
import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.schedule.ScheduleRow
import com.github.seepick.uscclient.utils.DateTimeRange
import com.github.seepick.uscclient.venue.VenueDetails
import com.github.seepick.uscclient.venue.VenueInfo
import com.github.seepick.uscclient.venue.VenuesFilter
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

class UscApiMock : UscApi {
    private val log = KotlinLogging.logger {}

    override suspend fun login(credentials: Credentials): LoginResult =
        LoginResult.Success(PhpSessionId("mockSessionId"))

    override suspend fun fetchVenues(session: PhpSessionId, filter: VenuesFilter): List<VenueInfo> {
        log.debug { "Mock returning empty venues list." }
        delay(500)
        return emptyList()
    }

    override suspend fun fetchVenueDetail(session: PhpSessionId, slug: String): VenueDetails {
        delay(500)
        return VenueDetails(
            title = "My Title",
            slug = "my-title",
            description = "mock description",
            linkedVenueSlugs = emptyList(),
            websiteUrl = null,
            disciplines = listOf("Yoga"),
            importantInfo = "impo info",
            openingTimes = "open altijd",
            longitude = "1.0",
            latitude = "2.0",
            originalImageUrl = URL("http://mock/test.png"),
            postalCode = "1001AA",
            streetAddress = "Main Street 42",
            addressLocality = "Amsterdam, Netherlands",
        )
    }

    override suspend fun fetchActivities(session: PhpSessionId, filter: ActivitiesFilter): List<ActivityInfo> {
        log.debug { "Mock returning empty activities list." }
        delay(500)
        return emptyList()
    }

    override suspend fun fetchActivityDetails(session: PhpSessionId, activityId: Int): ActivityDetails {
        delay(500)
        return ActivityDetails(
            name = "Cruz Sheppard",
            dateTimeRange = DateTimeRange(from = LocalDateTime.now(), to = LocalDateTime.now().plusHours(1)),
            venueName = "Hubert Welch",
            category = "vidisse",
            spotsLeft = 2,
            cancellationDateLimit = null,
            plan = Plan.UscPlan.Small,
            teacher = "Mock T.",
            description = "Mock description",
        )
    }

    override suspend fun fetchFreetrainings(session: PhpSessionId, filter: ActivitiesFilter): List<FreetrainingInfo> {
        log.debug { "Mock returning empty freetrainings list." }
        delay(500)
        return emptyList()
    }

    override suspend fun fetchFreetrainingDetails(session: PhpSessionId, freetrainingId: Int) = FreetrainingDetails(
        id = 7308,
        name = "Joann Dillard",
        date = LocalDate.now(),
        venueSlug = "aptent",
        category = "suscipit",
        plan = Plan.UscPlan.Medium,
    )

    override suspend fun fetchScheduleRows(session: PhpSessionId): List<ScheduleRow> {
        log.debug { "Mock returning empty schedule list." }
        delay(500)
        return emptyList()
    }

    override suspend fun fetchCheckinsPage(session: PhpSessionId, pageNr: Int, today: LocalDate): CheckinsPage {
        log.debug { "Mock returning empty checkins page." }
        delay(500)
        return CheckinsPage.Companion.empty
    }

    private var bookAlternator = true
    override suspend fun book(session: PhpSessionId, activityOrFreetrainingId: Int): BookingResult {
        log.info { "Mock booking: $activityOrFreetrainingId" }
        delay(1_000)
        bookAlternator = !bookAlternator
        return if (bookAlternator) BookingResult.BookingSuccess
        else BookingResult.BookingFail("Mock USC API says nope 🤪 but next time it will succeed.")
    }

    override suspend fun cancel(session: PhpSessionId, activityOrFreetrainingId: Int): CancelResult {
        log.info { "Mock cancel booking: $activityOrFreetrainingId" }
        delay(1_000)
        bookAlternator = !bookAlternator
        return if (bookAlternator) CancelResult.CancelSuccess
        else CancelResult.CancelFail("nope")
    }

    override suspend fun fetchMembership(session: PhpSessionId): Membership {
        return Membership(
            plan = Plan.OnefitPlan.Premium,
            country = Country.Companion.byLabel("Netherlands"),
            city = City.Companion.Amsterdam,
        )
    }
}

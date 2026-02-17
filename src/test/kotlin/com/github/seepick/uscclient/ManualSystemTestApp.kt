package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivitiesFilter
import com.github.seepick.uscclient.activity.ActivitiesParser
import com.github.seepick.uscclient.activity.ServiceType
import com.github.seepick.uscclient.model.City.Companion.Amsterdam
import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.venue.VenuesFilter
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDate

@Suppress("unused")
object ManualSystemTestApp {

    private val log = logger {}
    private val api = buildApiFacade()

    // https://urbansportsclub.com/en/venues/wilhelmina-gasthuisterrein
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            log.info { "Manual check running..." }
//            testFreetrainingDetails()
//            testVenues()
//            testVenue()
//            testActivity(92788662)
//            testActivities()
//            testSchedule()
//            testBook(84737975)
//            testMembership()
        }
    }

    private suspend fun testMembership() {
        val membership = api.fetchMembership()
        println("received membership: $membership")
    }

    private suspend fun testBook(activityId: Int) {
        val result = api.cancel(activityOrFreetrainingId = 84937551)
        //.book(activityId)
        println(result)
    }

    private suspend fun testFreetrainingDetails() {
        val freetrainingId = 83664090
        val result = api.fetchFreetrainingDetails(freetrainingId)
        println(result)
    }

    private suspend fun testVenue() {
        val slug = "amsterdam-noord"
        val details = api.fetchVenueDetail(slug)
        println("details.title=${details.title}")
//        println("details.websiteUrl=${details.websiteUrl}")
        println("details.linkedVenueSlugs=${details.linkedVenueSlugs}")
    }

    private suspend fun testVenues() {
        api.fetchVenues(VenuesFilter(city = Amsterdam, plan = Plan.OnefitPlan.Premium))
            .sortedBy { it.slug }
            .also { println("Received ${it.size} venues (without those missing from linkings)") }.forEach(::println)
    }

    private suspend fun testActivities() {
        val today = LocalDate.now()
        val pages = api.activityApi.fetchPages(
            session = api.phpSessionId,
            filter = ActivitiesFilter(city = Amsterdam, plan = Plan.OnefitPlan.Premium, date = today),
            serviceType = ServiceType.Courses,
        )
        println("Received ${pages.size} pages of activities.")
        val activities = pages.flatMap { page ->
            ActivitiesParser.parseContent(page.content, today)
        }
        println("In total ${activities.size} activities.")
//        activities.forEach { println("- $it") }
        activities.map { activity ->
            activity to api.fetchActivityDetails(activity.id)
        }.groupBy {
            it.first.venueSlug
        }
            .forEach { (venue, infoAndDetails) ->
                println("Venue: $venue")
                infoAndDetails.forEach { (info, detail) ->
                    val diff: String = if (detail.cancellationDateLimit != null) {
                        Duration.between(detail.cancellationDateLimit, info.dateTimeRange.from).toHours().toString()
                    } else "?"
                    println(
                        " $diff hours ... ${info.name} [${info.dateTimeRange}] // ${detail.cancellationDateLimit}"
                    )
                }
            }
    }

    private suspend fun testActivity(activityId: Int) {
        val activity = api.fetchActivityDetails(activityId)
        println(activity)

    }

    private suspend fun testSchedule() {
        val rows = api.fetchScheduleRows()
        println("Got ${rows.size} scheduled rows back.")
        rows.forEach { row ->
            println("- $row")
        }
    }
}

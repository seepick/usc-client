package seepick.localsportsclub.api

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.runBlocking
import seepick.localsportsclub.api.activity.ActivitiesFilter
import seepick.localsportsclub.api.activity.ActivitiesParser
import seepick.localsportsclub.api.activity.ActivityHttpApi
import seepick.localsportsclub.api.activity.ServiceType
import seepick.localsportsclub.api.booking.BookingHttpApi
import seepick.localsportsclub.api.checkin.CheckinHttpApi
import seepick.localsportsclub.api.plan.MembershipHttpApi
import seepick.localsportsclub.api.venue.VenueHttpApi
import seepick.localsportsclub.api.venue.VenueParser
import seepick.localsportsclub.api.venue.VenuesFilter
import seepick.localsportsclub.persistence.ExposedActivityRepo
import seepick.localsportsclub.persistence.ExposedSinglesRepo
import seepick.localsportsclub.service.date.SystemClock
import seepick.localsportsclub.service.httpClient
import seepick.localsportsclub.service.model.City
import seepick.localsportsclub.service.model.Credentials
import seepick.localsportsclub.service.model.Plan
import seepick.localsportsclub.service.singles.SinglesServiceImpl
import seepick.localsportsclub.sync.DummySyncProgress
import seepick.localsportsclub.tools.cliConnectToDatabase
import java.time.Duration
import java.time.LocalDate

@Suppress("unused")
object ManualSystemTests {

    private val log = logger {}
    private val uscConfig = UscConfig(
        storeResponses = false,
    )
    private val responseStorage = ResponseStorageImpl()
    private val phpSessionId: PhpSessionId by lazy { runBlocking { loadSessionId() } }
    private val syncProgress = DummySyncProgress

    // https://urbansportsclub.com/en/venues/wilhelmina-gasthuisterrein
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            log.info { "Manual test running..." }
//            testFreetrainingDetails()
//            testCheckins()
//            testVenues()
//            testVenue()
            testActivity(92788662)
//            testActivities()
//            testSchedule()
//            testBook(84737975)
//            testMembership()
        }
    }

    private fun activityApi() = ActivityHttpApi(httpClient, responseStorage, uscConfig, SystemClock)
    private fun checkinApi() = CheckinHttpApi(httpClient, responseStorage, SystemClock, uscConfig)
    private fun venueApi() = VenueHttpApi(httpClient, responseStorage, uscConfig, syncProgress)
    private fun bookingApi() = BookingHttpApi(httpClient, uscConfig, responseStorage)

    private suspend fun testMembership() {
        val membership = MembershipHttpApi(httpClient, responseStorage, uscConfig).fetch(phpSessionId)
        println("received membership: $membership")
    }

    private suspend fun testBook(activityId: Int) {
        val result = bookingApi().cancel(phpSessionId, 84937551)
        //.book(activityId)
        println(result)
    }

    private suspend fun testFreetrainingDetails() {
        val freetrainingId = 83664090
        val result = activityApi().fetchFreetrainingDetails(phpSessionId, freetrainingId)
        println(result)
    }

    private suspend fun testCheckins() {
        val response = checkinApi().fetchPage(phpSessionId, 1)
        println("received ${response.entries.size} checkins")
        response.entries.forEach { entry ->
            println(entry)
        }
    }

    private suspend fun testVenue() {
        val slug = "amsterdam-noord"
        val details = venueApi().fetchDetails(phpSessionId, slug)
        println("details.title=${details.title}")
//        println("details.websiteUrl=${details.websiteUrl}")
        println("details.linkedVenueSlugs=${details.linkedVenueSlugs}")
    }

    private suspend fun testVenues() {
        val pages = venueApi().fetchPages(
            phpSessionId,
            VenuesFilter(
                city = City.Amsterdam, plan = Plan.OnefitPlan.Premium
            )
        )
        pages.flatMap { VenueParser.parseHtmlContent(it.content) }.sortedBy { it.slug }
            .also { println("Received ${it.size} venues (without those missing from linkings)") }.forEach(::println)
    }

    private suspend fun testActivities() {
        val today = LocalDate.now()
        val api = activityApi()
        val pages = api.fetchPages(
            phpSessionId,
            filter = ActivitiesFilter(city = City.Amsterdam, plan = Plan.OnefitPlan.Premium, date = today),
            serviceType = ServiceType.Courses,
        )
        println("Received ${pages.size} pages of activities.")
        val activities = pages.flatMap { page ->
            ActivitiesParser.parseContent(page.content, today)
        }
        println("In total ${activities.size} activities.")
//        activities.forEach { println("- $it") }
        activities.map { activity ->
            activity to api.fetchActivityDetails(phpSessionId, activity.id)
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
        val activity = activityApi().fetchActivityDetails(phpSessionId, activityId)
        println(activity)

    }

    private suspend fun testSchedule() {
        val booked = ExposedActivityRepo.selectAllBooked(City.Amsterdam.id)
        println("Got ${booked.size} locally booked activities.")
        booked.forEach { row ->
            println("- $row")
        }

//        val rows = ScheduleHttpApi(httpClient, responseStorage, uscConfig).fetchScheduleRows(phpSessionId)
//        println("Got ${rows.size} scheduled rows back.")
//        rows.forEach { row ->
//            println("- $row")
//        }
    }

    private suspend fun loadSessionId(): PhpSessionId {
        val syspropSessionId = System.getProperty("phpSessionId")
        if (syspropSessionId != null) {
            println("Using system property's session ID: $syspropSessionId")
            return PhpSessionId(syspropSessionId)
        }
        val syspropUsername = System.getProperty("username")
        val syspropPassword = System.getProperty("password")
        val credentials = if (syspropUsername != null && syspropPassword != null) {
            println("Using credentials from system property.")
            Credentials(syspropUsername, syspropPassword)
        } else {
            cliConnectToDatabase(isProd = false)
            println("Using credentials from exposed repository.")
            SinglesServiceImpl(ExposedSinglesRepo).preferences.uscCredentials ?: error("No credentials stored in DB")
        }
        return LoginHttpApi(httpClient, uscConfig.baseUrl).login(credentials)
            .shouldBeInstanceOf<LoginResult.Success>().phpSessionId.also {
                println("New PHP session ID is: $it")
            }
    }

}

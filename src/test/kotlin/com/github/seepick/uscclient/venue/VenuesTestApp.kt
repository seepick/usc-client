package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.buildApiFacade
import com.github.seepick.uscclient.model.City.Companion.Amsterdam
import com.github.seepick.uscclient.plan.Plan
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking

object VenuesTestApp {

    private val log = logger {}
    private val api = buildApiFacade()

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            testVenues()
        }
    }

    private suspend fun testVenue() {
        val slug = "amsterdam-noord"
        val details = api.fetchVenueDetail(slug)
        println("details.title=${details.title}")
//        println("details.websiteUrl=${details.websiteUrl}")
        println("details.linkedVenueSlugs=${details.linkedVenueSlugs}")
    }

    private suspend fun testVenues() {
        val venues = api.fetchVenues(VenuesFilter(city = Amsterdam, plan = Plan.OnefitPlan.Premium))
//            .sortedBy { it.slug }
        println("Received ${venues.size} venues (without those missing from linkings)")
        venues.forEach {
            println("- $it")
        }
    }
}

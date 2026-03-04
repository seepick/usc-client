package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.buildApiFacade
import com.github.seepick.uscclient.model.City.Companion.Amsterdam
import com.github.seepick.uscclient.plan.Plan
import kotlinx.coroutines.runBlocking

@Suppress("unused")
object VenuesTestApp {

    private val api = buildApiFacade()

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
//            testVenues()
            testVenue()
        }
    }

    private suspend fun testVenue() {
        val slug = "equal-yoga-rozengracht"
        val details = api.fetchVenueDetail(slug)
        println(details)
    }

    private suspend fun testVenues() {
        val venues = api.fetchVenues(VenuesFilter(city = Amsterdam, plan = Plan.OnefitPlan.Premium))
        println("Received ${venues.size} venues (without those missing from linkings)")
        venues.sortedBy { it.title }.forEach {
            println("* ${it.title} [${it.slug}] (${it.plan.apiString})")
        }
    }
}

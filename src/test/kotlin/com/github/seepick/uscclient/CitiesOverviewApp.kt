package com.github.seepick.uscclient

import com.github.seepick.uscclient.model.CitiesCountries
import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.venue.VenuesFilter
import kotlinx.coroutines.runBlocking

object CitiesOverviewApp {

    private val api = buildApiFacade()

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val dutchCities = CitiesCountries.allCountries.single { it.label == "Netherlands" }.cities
            val citiesAndVenuesCount = dutchCities.map { withVenuesCount(it) }
            println()
            citiesAndVenuesCount.forEach { (city, venueCount) ->
                println("* ${city.label} => $venueCount")
            }
            println()
        }
    }

    private suspend fun withVenuesCount(city: City): Pair<City, Int> {
        val venues = api.fetchVenues(VenuesFilter(city, Plan.UscPlan.ExtraLarge))
        return city to venues.size
    }
}

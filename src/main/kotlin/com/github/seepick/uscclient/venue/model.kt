package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.plan.Plan
import java.net.URL

public data class VenueInfo(
    val title: String,
    val slug: String, // e.g. to be used as "/en/venues/{slug}"
    val imageUrl: URL?,
    val disciplines: List<String>,
    val addressId: Int,
    val addressDistrict: String,
    val addressStreet: String,
    val plan: Plan.UscPlan,
)

public data class VenueDetails(
    val title: String, // a.k.a. "name"
    val slug: String,
    val description: String,
    val disciplines: List<String>,
    val linkedVenueSlugs: List<String>,
    val websiteUrl: URL?, // the official one, not the USC own (inferred by slug and static URL prefix)
    val importantInfo: String?,
    val openingTimes: String?,
    val postalCode: String, // from JSON script
    val streetAddress: String, // from JSON script
    val addressLocality: String, // from JSON script
    val latitude: String, // from JSON script
    val longitude: String,
    val originalImageUrl: URL?, // from JSON script
    // doesn't have a plan
    // multiple pictures ... maybe in the future
    // visit limits ... always the same
)

public data class VenuesFilter(
    val city: City,
    val plan: Plan,
)

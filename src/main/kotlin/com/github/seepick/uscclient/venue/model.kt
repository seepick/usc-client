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
    val carouselUrls: List<URL>,
    val visitLimits: VisitLimits?,
    // doesn't have a plan
)

public data class VenuesFilter(
    val city: City,
    val plan: Plan,
)

public data class VisitLimits(
    val small: Int,
    val medium: Int,
    val large: Int,
    val xlarge: Int,
) {
    public fun forPlan(plan: Plan.UscPlan): Int =
        when (plan) {
            Plan.UscPlan.Small -> small
            Plan.UscPlan.Medium -> medium
            Plan.UscPlan.Large -> large
            Plan.UscPlan.ExtraLarge -> xlarge
        }

    companion object // for extensions
}

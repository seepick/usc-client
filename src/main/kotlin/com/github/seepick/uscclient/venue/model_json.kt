package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.shared.StatsJson
import com.github.seepick.uscclient.shared.Pageable
import kotlinx.serialization.Serializable

@Serializable
internal data class VenuesJson(
    val success: Boolean,
    val data: VenuesDataJson,
)

@Serializable
internal data class VenuesDataJson(
    override val showMore: Boolean,
    val content: String, // HTML
    val stats: StatsJson, // summary of used data in this response
    val searchExecutedEvent: String, // big JSON
    val regionSelectorSelected: String?, // ... what type?!
) : Pageable

@Serializable
internal data class VenueDetailEmbedJson(
    val telephone: String, val image: String, val address: VenueDetailEmbedAddress, val geo: VenueDetailEmbedGeo,
)

@Serializable
internal data class VenueDetailEmbedAddress(
    val postalCode: String,
    val streetAddress: String,
    val addressLocality: String,
)

@Serializable
internal data class VenueDetailEmbedGeo(
    val latitude: String,
    val longitude: String,
)

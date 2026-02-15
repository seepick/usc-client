package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.Pageable
import com.github.seepick.uscclient.StatsJson
import kotlinx.serialization.Serializable

@Serializable
data class VenuesJson(
    val success: Boolean,
    val data: VenuesDataJson,
)

@Serializable
data class VenuesDataJson(
    override val showMore: Boolean,
    val content: String, // HTML
    val stats: StatsJson, // summary of used data in this response
    val searchExecutedEvent: String, // big JSON
    val regionSelectorSelected: String?, // ... what type?!
) : Pageable

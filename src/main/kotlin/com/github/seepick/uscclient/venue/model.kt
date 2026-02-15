package seepick.localsportsclub.api.venue

import kotlinx.serialization.Serializable
import seepick.localsportsclub.api.Pageable
import seepick.localsportsclub.api.StatsJson

@Serializable
data class VenuesJson(
    val success: Boolean,
    val data: VenuesDataJson
)

@Serializable
data class VenuesDataJson(
    override val showMore: Boolean,
    val content: String, // HTML
    val stats: StatsJson, // summary of used data in this response
    val searchExecutedEvent: String, // big JSON
    val regionSelectorSelected: String? // ... what type?!
) : Pageable

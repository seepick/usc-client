package seepick.localsportsclub.api.activity

import kotlinx.serialization.Serializable
import seepick.localsportsclub.api.Pageable
import seepick.localsportsclub.api.StatsJson

@Serializable
data class ActivitiesJson(
    val success: Boolean,
    val data: ActivitiesDataJson,
)

@Serializable
data class ActivitiesDataJson(
    override val showMore: Boolean,
    val content: String, // HTML
    val stats: StatsJson,
    val emptySnippet: String?, // what type?
    val searchExecutedEvent: String, // big JSON
    val regionSelectorSelected: String? // what type?!
) : Pageable

package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.Pageable
import com.github.seepick.uscclient.StatsJson
import kotlinx.serialization.Serializable

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
    val regionSelectorSelected: String?, // what type?!
) : Pageable

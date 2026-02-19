package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.shared.Pageable
import com.github.seepick.uscclient.shared.StatsJson
import kotlinx.serialization.Serializable

@Serializable
internal data class ActivityDataLayerJson(
    // event: String,
    // user: ...,
    val `class`: ActivityDataLayerClassJson,
)

@Serializable
internal data class ActivitiesJson(
    val success: Boolean,
    val data: ActivitiesDataJson,
)

@Serializable
internal data class ActivitiesDataJson(
    override val showMore: Boolean,
    val content: String, // HTML
    val stats: StatsJson,
    val emptySnippet: String?, // what type?
    val searchExecutedEvent: String, // big JSON
    val regionSelectorSelected: String?, // what type?!
) : Pageable

@Serializable
internal data class ActivityDataLayerClassJson(
    val id: String,
    val name: String,
    val category: String,
    val spots_left: String,
)

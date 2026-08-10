package com.github.seepick.uscclient.shared

import kotlinx.serialization.Serializable

@Serializable
internal data class StatsJson(
    val category: List<StatsCategoryJson>,
    val district: StatsDistrictJson,
    val venue: List<StatsVenueJson>,
)

@Serializable
internal data class StatsCategoryJson(
    val name: String,
    val attributes: StatsCategoryAttributesJson,
)

@Serializable
internal data class StatsCategoryAttributesJson(
    val value: String,
)

@Serializable
internal data class StatsDistrictJson(
    val districts: List<StatsDistrictDistrictJson>,
//  skip this as not used... val areas: List<StatsDistrictAreaJson>
)

@Serializable
internal data class StatsDistrictDistrictJson(
    val name: String,
    val attributes: StatsDistrictAreaAtributesValueJson,
)

@Serializable
internal data class StatsDistrictAreaAtributesValueJson(
    val value: Int,
)

@Serializable
internal data class StatsVenueJson(
    val name: String,
    val attributes: StatsVenueAttributesJson,
)

@Serializable
internal data class StatsVenueAttributesJson(
    val value: Int,
)

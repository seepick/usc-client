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
    val district: List<StatsDistrictDistrictJson>,
    val areas: List<StatsDistrictAreaJson>,
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
internal data class StatsDistrictAreaJson(
    val name: String,
    val attributes: StatsDistrictAreaAtributesJson,
    val districts: List<StatsDistrictAreaDistrictJson>,
)

@Serializable
internal data class StatsDistrictAreaAtributesJson(
    val value: Int,
    val `class`: String,
)

@Serializable
internal data class StatsDistrictAreaDistrictJson(
    val name: String,
    val attributes: StatsDistrictAreaAtributesJson,
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

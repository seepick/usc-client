package seepick.localsportsclub.api

import kotlinx.serialization.Serializable

@Serializable
data class StatsJson(
    val category: List<StatsCategoryJson>,
    val district: StatsDistrictJson,
    val venue: List<StatsVenueJson>,
)

@Serializable
data class StatsCategoryJson(
    val name: String,
    val attributes: StatsCategoryAttributesJson,
)

@Serializable
data class StatsCategoryAttributesJson(
    val value: String,
)

@Serializable
data class StatsDistrictJson(
    val district: List<StatsDistrictDistrictJson>,
    val areas: List<StatsDistrictAreaJson>,
)

@Serializable
data class StatsDistrictDistrictJson(
    val name: String,
    val attributes: StatsDistrictAreaAtributesValueJson,
)

@Serializable
data class StatsDistrictAreaAtributesValueJson(
    val value: Int,
)

@Serializable
data class StatsDistrictAreaJson(
    val name: String,
    val attributes: StatsDistrictAreaAtributesJson,
    val districts: List<StatsDistrictAreaDistrictJson>,
)

@Serializable
data class StatsDistrictAreaAtributesJson(
    val value: Int,
    val `class`: String,
)

@Serializable
data class StatsDistrictAreaDistrictJson(
    val name: String,
    val attributes: StatsDistrictAreaAtributesJson,
)

@Serializable
data class StatsVenueJson(
    val name: String,
    val attributes: StatsVenueAttributesJson,
)

@Serializable
data class StatsVenueAttributesJson(
    val value: Int,
)

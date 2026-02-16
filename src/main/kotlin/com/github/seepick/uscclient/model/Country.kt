package com.github.seepick.uscclient.model

public data class Country(
    val label: String,
    val cities: List<City>,
) {
    companion object {
        val all: List<Country> by lazy { CitiesCountries.allCountries }
        fun byCityId(cityId: Int): Country =
            all.firstOrNull { it.cities.any { it.id == cityId } } ?: error("Country not found by city ID $cityId")

        fun byLabel(label: String): Country =
            all.firstOrNull { it.label == label } ?: error("Country not found by label [$label]")
    }

    override fun toString() = "City[label=$label, cities.size=${cities.size}]"
}

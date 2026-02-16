package com.github.seepick.uscclient.model

public data class City(
    val id: Int,
    val label: String,
) {
    companion object {
        val all: List<City> by lazy { CitiesCountries.allCountries.flatMap { it.cities } }
        val Amsterdam by lazy { byLabel("Amsterdam") }
        val Berlin by lazy { byLabel("Berlin") }
        fun byId(cityId: Int) = all.firstOrNull { it.id == cityId } ?: error("City not found by ID $cityId")
        fun byLabel(label: String) = all.firstOrNull { it.label == label } ?: error("City not found by label [$label]")
    }
}

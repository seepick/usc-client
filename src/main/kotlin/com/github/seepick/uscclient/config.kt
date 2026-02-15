package com.github.seepick.uscclient

import io.ktor.http.Url

data class UscConfig(
    val baseUrl: Url = Url("https://urbansportsclub.com/${UscLang.English.urlCode}"),
    val storeResponses: Boolean = true,
    val syncDaysAhead: Int = 14, // including today
) {
    init {
        require(syncDaysAhead >= 1) { "sync days ahead must be >= 1 but was: $syncDaysAhead" }
    }
}

package com.github.seepick.uscclient

import com.github.seepick.uscclient.model.UscLang
import java.io.File
import java.net.URL

data class ApiConfig(
    val apiMode: ApiMode,
//    val sync: SyncMode,
    val usc: UscConfig = UscConfig(),
//    val logFileEnabled: Boolean = false,
    val responseLogFolder: File, // api logs
)

enum class ApiMode {
    Mock, RealHttp
}

// TODO merge both Config classes together
data class UscConfig(
    // TODO allow to pass UscLang only
    val baseUrl: URL = URL("https://urbansportsclub.com/${UscLang.English.urlCode}"),
    val storeResponses: Boolean = true,
    val syncDaysAhead: Int = 14, // including today
) {
    init {
        require(syncDaysAhead >= 1) { "sync days ahead must be >= 1 but was: $syncDaysAhead" }
    }
}

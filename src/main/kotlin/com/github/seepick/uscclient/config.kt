package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.model.UscLang
import java.io.File

public data class UscConfig(
    val credentials: Credentials,
    val lang: UscLang = UscLang.English,
//    val logFileEnabled: Boolean = false,
//    val storeResponses: Boolean = true,
    val responseLogFolder: File?, // api logs
    val currentYear: Int,
)

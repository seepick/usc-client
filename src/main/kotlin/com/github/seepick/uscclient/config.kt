package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.model.UscLang
import java.io.File
import java.net.URL

public data class UscConfig(
    val credentials: Credentials,
    val responseLogFolder: File?, // api logs
    val currentYear: Int, // parsing necessity
) {
    val baseUrl = UscLang.singleSupported.baseUrl
}

public val UscLang.baseUrl get() = URL("https://urbansportsclub.com/$urlCode/")

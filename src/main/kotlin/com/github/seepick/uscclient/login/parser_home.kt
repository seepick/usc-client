package com.github.seepick.uscclient.login

import org.jsoup.Jsoup

internal object HomePageParser {
    fun parse(html: String): HomeHtmlResponse {
        val body = Jsoup.parse(html).body()
        val login = body.getElementById("login-form") ?: error("login-form not found in HTML response:\n\n$html")
        val secret = login.getElementsByTag("input").single {
            it.attr("type") == "hidden" && it.id() != "check"
        }
        return HomeHtmlResponse(
            loginSecret = secret.attr("name") to secret.attr("value"),
        )
    }
}

/** hidden input in the login form, which needs to be passed through */
internal data class HomeHtmlResponse(
    val loginSecret: Pair<String, String>,
)

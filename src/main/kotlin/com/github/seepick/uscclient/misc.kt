package com.github.seepick.uscclient

data class Credentials(
    val username: String,
    val password: String,
) {
    override fun toString() = "Credentials[username=$username, password=***]"
}

enum class UscLang(val urlCode: String) {
    English("en"),
    Dutch("nl"),
//    German("de"),
//    French("fr"),
    // PT, ES
}

enum class ApiMode {
    Mock, RealHttp
}

fun String.unescape(): String = replace("\\\"", "\"").replace("\\n", "\n")

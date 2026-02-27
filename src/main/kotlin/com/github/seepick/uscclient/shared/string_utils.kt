package com.github.seepick.uscclient.shared

internal fun String.unescape(): String =
    replace("\\\"", "\"").replace("\\n", "\n")

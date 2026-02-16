package com.github.seepick.uscclient.utils

internal fun String.unescape(): String =
    replace("\\\"", "\"").replace("\\n", "\n")

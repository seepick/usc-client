package com.github.seepick.uscclient

public open class UscException(
    message: String,
    cause: Exception? = null,
) : Exception(message, cause)

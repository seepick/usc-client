package com.github.seepick.uscclient

public open class UscException(
    message: String,
    reason: UscErrorReason,
    cause: Exception? = null,
) : Exception(message, cause)

public sealed class UscErrorReason {
    object LoginFailed : UscErrorReason()
    object JsonSuccessFalse : UscErrorReason()
    data class InvalidStatusCode(val statusCode: Int) : UscErrorReason()
}

package com.github.seepick.uscclient.booking

public sealed interface BookingResult {
    data object BookingSuccess : BookingResult
    data class BookingFail(val message: String) : BookingResult
}

public sealed interface CancelResult {
    data object CancelSuccess : CancelResult
    data class CancelFail(val message: String) : CancelResult
}

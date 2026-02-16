package com.github.seepick.uscclient.booking

import kotlinx.serialization.Serializable

@Serializable
internal data class BookingSuccessResponseJson(
    val success: Boolean,
    val data: BookingSuccessDataJson,
)

@Serializable
internal data class BookingSuccessDataJson(
    val id: Int,
    val state: String, // "booked" == activity, "scheduled" == freetraining
    // label, alert, isManual, cancelButton
    val freeSpots: FreeSpotsJson,
)

@Serializable
internal data class FreeSpotsJson(
    val current: Int,
    val maximum: Int,
)

@Serializable
internal data class BookingErrorResponseJson(
    val success: Boolean,
    val data: BookingErrorDataJson,
)

@Serializable
internal data class BookingErrorDataJson(
    val state: String, // "error"
    val alert: String,
)

@Serializable
internal data class CancellationSuccessResponseJson(
    val success: Boolean,
    val data: CancellationSuccessDataJson,
)

@Serializable
internal data class CancellationSuccessDataJson(
    val id: Int,
    val state: String, // "cancel_customer"
    // label, alert
    val freeSpots: FreeSpotsJson,
)

@Serializable
internal data class CancellationErrorResponseJson(
    val success: Boolean,
    val data: CancellationErrorDataJson,
)

@Serializable
internal data class CancellationErrorDataJson(
    val state: String, // "error"
    val alert: String, // error detail message
)

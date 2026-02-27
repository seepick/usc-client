package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.shared.DateTimeRange
import java.time.LocalDate
import java.time.LocalDateTime

public data class ActivitiesFilter(
    val city: City,
    val plan: Plan,
    val date: LocalDate,
) {
    companion object // for extensions
}

public data class ActivityInfo(
    val id: Int,
    val name: String,
    val venueSlug: String,
    val dateTimeRange: DateTimeRange,
    val category: String, // aka disciplines/facilities
    val spotsLeft: Int,
    val plan: Plan.UscPlan,
    // type = "instant booking"
) {
    companion object // for extensions
}

public data class ActivityDetails(
    val name: String,
    val dateTimeRange: DateTimeRange,
    val venueName: String,
    val plan: Plan.UscPlan,
    val category: String,
    val spotsLeft: Int,
    val cancellationDateLimit: LocalDateTime?,
    val teacher: String?,
    val description: String?,
) {
    companion object // for extensions
}

public data class FreetrainingInfo(
    val id: Int,
    val name: String,
    val category: String,
    val venueSlug: String,
    val plan: Plan.UscPlan,
) {
    companion object // for extensions
}

public data class FreetrainingDetails(
    val id: Int,
    val name: String,
    val date: LocalDate,
    val venueSlug: String,
    val category: String,
    val plan: Plan.UscPlan,
) {
    companion object // for extensions
}

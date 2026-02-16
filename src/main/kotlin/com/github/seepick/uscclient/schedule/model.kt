package com.github.seepick.uscclient.schedule

// TODO redesign as sealed type
public data class ScheduleRow(
    val activityOrFreetrainingId: Int,
    val venueSlug: String,
    val entityType: ScheduleEntityType,
)

public enum class ScheduleEntityType {
    Activity, Freetraining
}

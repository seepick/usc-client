package com.github.seepick.uscclient.checkin

import com.github.seepick.uscclient.utils.TimeRange
import java.time.LocalDate

public data class CheckinsPage(
    val entries: List<CheckinEntry>,
) {
    companion object {
        val empty = CheckinsPage(emptyList())
    }

    val isEmpty = entries.isEmpty()
}

public sealed interface CheckinEntry {
    val venueSlug: String
    val date: LocalDate
}

public data class ActivityCheckinEntry(
    val activityId: Int,
    override val venueSlug: String,
    override val date: LocalDate,
    val timeRange: TimeRange,
    val type: ActivityCheckinEntryType,
) : CheckinEntry

public enum class ActivityCheckinEntryType {
    Checkedin,
    Noshow,
    CancelledLate;
}

public data class FreetrainingCheckinEntry(
    val freetrainingId: Int,
    override val venueSlug: String,
    override val date: LocalDate,
) : CheckinEntry

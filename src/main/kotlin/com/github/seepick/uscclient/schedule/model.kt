package com.github.seepick.uscclient.schedule

public sealed interface BookedOrScheduled {
    val id: Int
    val venueSlug: String
}

public data class BookedActivity(
    val activityId: Int,
    override val venueSlug: String,
) : BookedOrScheduled {
    override val id = activityId
}

public data class ScheduledFreetraining(
    val freetrainingId: Int,
    override val venueSlug: String,
) : BookedOrScheduled {
    override val id = freetrainingId
}

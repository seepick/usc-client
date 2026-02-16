package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.checkin.ActivityCheckinEntry
import com.github.seepick.uscclient.checkin.ActivityCheckinEntryType
import com.github.seepick.uscclient.dateTimeRange
import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.slug
import com.github.seepick.uscclient.timeRange
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.string

fun Arb.Companion.activityInfo() = arbitrary {
    ActivityInfo(
        id = int(min = 1).bind(),
        venueSlug = slug().bind(),
        name = string(minSize = 5, maxSize = 20).bind(),
        category = string(minSize = 1, maxSize = 5, codepoints = Codepoint.az()).bind(),
        spotsLeft = int(min = 0, max = 10).bind(),
        dateTimeRange = dateTimeRange().bind(),
        plan = enum<Plan.UscPlan>().bind(),
    )
}

fun Arb.Companion.activityCheckinEntry() = arbitrary {
    ActivityCheckinEntry(
        activityId = int(min = 1).bind(),
        venueSlug = slug().bind(),
        date = localDate().bind(),
        timeRange = timeRange().bind(),
        type = enum<ActivityCheckinEntryType>().bind(),
    )
}

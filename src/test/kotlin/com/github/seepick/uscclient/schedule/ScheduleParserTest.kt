package com.github.seepick.uscclient.schedule

import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual

class ScheduleParserTest : StringSpec({

    fun parseSchedule(fileName: String) =
        ScheduleParser.parse(readTestResponse(fileName))

    "When parse booked activities Then return entries" {
        parseSchedule("schedule.activities.html") shouldBeEqual listOf(
            BookedActivity(84742854, "studio-108-3"),
            BookedActivity(84726253, "yoga-spot-olympisch-stadion"),
            BookedActivity(84810748, "movements-city"),
        )
    }
    "When parse scheduled freetraining Then return entry" {
        parseSchedule("schedule.freetraining.html") shouldBeEqual listOf(
            ScheduledFreetraining(83664089, "vitality-spa-fitness-amsterdam"),
        )
    }
})

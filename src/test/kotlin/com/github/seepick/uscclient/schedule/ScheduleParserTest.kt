package com.github.seepick.uscclient.schedule

import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual

class ScheduleParserTest : StringSpec() {
    private fun parseSchedule(fileName: String) = ScheduleParser.parse(readTestResponse(fileName))

    init {
        "When parse booked activities Then return entries" {
            val schedule = parseSchedule("schedule.activities.html")

            schedule shouldBeEqual listOf(
                ScheduleRow(84742854, "studio-108-3", ScheduleEntityType.Activity),
                ScheduleRow(84726253, "yoga-spot-olympisch-stadion", ScheduleEntityType.Activity),
                ScheduleRow(84810748, "movements-city", ScheduleEntityType.Activity),
            )
        }
        "When parse scheduled freetraining Then return entry" {
            val schedule = parseSchedule("schedule.freetraining.html")

            schedule shouldBeEqual listOf(
                ScheduleRow(83664089, "vitality-spa-fitness-amsterdam", ScheduleEntityType.Freetraining),
            )
        }
    }
}

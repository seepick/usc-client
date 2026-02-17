package com.github.seepick.uscclient.schedule

import com.github.seepick.uscclient.shared.JsoupUtil
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal object ScheduleParser {

    private val log = logger {}

    fun parse(html: String): List<ScheduleRow> {
        val body = JsoupUtil.extractBody(html)
        val divs = body.select("div.reservations div.timetable div[class=\"smm-class-snippet  row\"]").toList()

        return divs.map { div ->
            ScheduleRow(
                activityOrFreetrainingId = div.attr("data-appointment-id").toInt(),
                venueSlug = div.select("a.smm-studio-link").attr("href").substringAfterLast("/"),
                entityType = div.select("span.smm-booking-state-label").let {
                    if (it.hasClass("booked")) {
                        ScheduleEntityType.Activity
                    } else if (it.hasClass("scheduled")) {
                        ScheduleEntityType.Freetraining
                    } else error(
                        "Couldn't determine whether it's an activity or freetraining based on CSS classes: ${
                            it.attr("class")
                        }"
                    )
                }
            )
        }.also {
            log.debug { "Parsed ${divs.size} reservation <div> tags to: $it" }
        }
    }
}

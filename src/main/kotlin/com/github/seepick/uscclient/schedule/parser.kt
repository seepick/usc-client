package com.github.seepick.uscclient.schedule

import com.github.seepick.uscclient.shared.JsoupUtil
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal object ScheduleParser {

    private val log = logger {}

    fun parse(html: String): List<BookedOrScheduled> {
        val body = JsoupUtil.extractBody(html)
        val divs = body.select("div.reservations div.timetable div[class=\"smm-class-snippet  row\"]").toList()

        return divs.map { div ->
            val entityId = div.attr("data-appointment-id").toInt()
            val slug = div.select("a.smm-studio-link").attr("href").substringAfterLast("/")
            val bookingLabelType = div.select("span.smm-booking-state-label")
            if (bookingLabelType.hasClass("booked")) {
                BookedActivity(entityId, slug)
            } else if (bookingLabelType.hasClass("scheduled")) {
                ScheduledFreetraining(entityId, slug)
            } else error(
                "Couldn't determine whether it's an activity or freetraining based on CSS classes: ${
                    bookingLabelType.attr("class")
                }"
            )

        }.also {
            log.debug { "Parsed ${divs.size} reservation <div> tags to: $it" }
        }
    }
}

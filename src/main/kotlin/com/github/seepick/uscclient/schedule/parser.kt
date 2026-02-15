package seepick.localsportsclub.api.schedule

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import seepick.localsportsclub.service.jsoupBody
import seepick.localsportsclub.service.model.EntityType

data class ScheduleHtml(
    val rows: List<ScheduleRow>
)

data class ScheduleRow(
    val activityOrFreetrainingId: Int,
    val venueSlug: String,
    val entityType: EntityType,
)

object ScheduleParser {

    private val log = logger {}

    fun parse(html: String): ScheduleHtml {
        val body = jsoupBody(html)
        val divs = body.select("div.reservations div.timetable div[class=\"smm-class-snippet row\"]").toList()

        return ScheduleHtml(rows = divs.map { div ->
            ScheduleRow(
                activityOrFreetrainingId = div.attr("data-appointment-id").toInt(),
                venueSlug = div.select("a.smm-studio-link").attr("href").substringAfterLast("/"),
                entityType = div.select("span.smm-booking-state-label").let {
                    if (it.hasClass("booked")) {
                        EntityType.Activity
                    } else if (it.hasClass("scheduled")) {
                        EntityType.Freetraining
                    } else error(
                        "Couldn't determine whether it's an activity or freetraining based on CSS classes: ${
                            it.attr("class")
                        }"
                    )

                }
            )
        }).also {
            log.debug { "Parsed ${divs.size} reservation <div> tags to: $it" }
        }
    }
}

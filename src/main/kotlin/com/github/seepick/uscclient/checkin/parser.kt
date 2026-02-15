package seepick.localsportsclub.api.checkin

import seepick.localsportsclub.service.date.DateParser
import seepick.localsportsclub.service.date.TimeRange
import seepick.localsportsclub.service.jsoupBody
import java.time.LocalDate

data class CheckinsPage(
    val entries: List<CheckinEntry>,
) {
    companion object {
        val empty = CheckinsPage(emptyList())
    }

    val isEmpty = entries.isEmpty()
}

sealed interface CheckinEntry {
    val venueSlug: String
    val date: LocalDate
}

enum class ActivityCheckinEntryType {
    Checkedin, Noshow, CancelledLate;
}

data class ActivityCheckinEntry(
    val activityId: Int,
    override val venueSlug: String,
    override val date: LocalDate,
    val timeRange: TimeRange,
    val type: ActivityCheckinEntryType,
) : CheckinEntry

data class FreetrainingCheckinEntry(
    val freetrainingId: Int,
    override val venueSlug: String,
    override val date: LocalDate,
) : CheckinEntry

object CheckinsParser {
    fun parse(rawHtml: String, today: LocalDate): CheckinsPage {
        val body = jsoupBody(rawHtml)
        var currentDate: LocalDate? = null
        val entries = mutableListOf<CheckinEntry>()
        val timetable = body.select("div.timetable").first() ?: return CheckinsPage.empty
        timetable.children().forEach { sub ->
            when (sub.attr("class")) {
                "table-date" -> {
                    currentDate = DateParser.parseDate(sub.text().trim(), today.year).let {
                        // transitioning to next year
                        if (it <= today) it else it.withYear(today.year - 1)
                    }
                }

                "smm-class-snippet row" -> {
                    val id = sub.attr("data-appointment-id").toInt()
                    val time = sub.select("p.smm-class-snippet__class-time")
                    val venueSlug = sub.select("a.smm-studio-link").first()!!.attr("href").substringAfterLast("/")
                    entries += if (time.isEmpty()) { // it's a freetraining checkin as we got no time info for it
                        FreetrainingCheckinEntry(
                            date = currentDate!!,
                            freetrainingId = id,
                            venueSlug = venueSlug,
                        )
                    } else {
                        val select = sub.select("span.smm-booking-state-label")
                        val type = if (select.hasClass("noshow")) {
                            ActivityCheckinEntryType.Noshow
                        } else if (select.hasClass("late")) {
                            ActivityCheckinEntryType.CancelledLate
                        } else if (select.hasClass("done")) {
                            ActivityCheckinEntryType.Checkedin
                        } else {
                            error("Could not determine activity checkin type by CSS class: ${select.attr("class")}")
                        }
                        ActivityCheckinEntry(
                            date = currentDate!!,
                            activityId = id,
                            venueSlug = venueSlug,
                            timeRange = DateParser.parseTimes(sub.select("p.smm-class-snippet__class-time").text()),
                            type = type,
                        )
                    }
                }
            }
        }
        return CheckinsPage(entries)
    }
}

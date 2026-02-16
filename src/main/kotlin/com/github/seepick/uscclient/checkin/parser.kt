package com.github.seepick.uscclient.checkin

import com.github.seepick.uscclient.utils.DateParser
import com.github.seepick.uscclient.shared.JsoupUtil
import java.time.LocalDate

internal object CheckinsParser {
    fun parse(rawHtml: String, today: LocalDate): CheckinsPage {
        val body = JsoupUtil.extractBody(rawHtml)
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

package com.github.seepick.uscclient.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.util.Locale

internal object DateParser {

    private val dayMonthFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH)
    private val timeParser = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH)
    private val parseEuropeDate = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)

    /**
     * @param dateString e.g. "Friday, 27 December"
     */
    fun parseDate(dateString: String, year: Int): LocalDate {
        val dateTemporal = dayMonthFormatter.parse(dateString)
        return LocalDate.of(year, dateTemporal[ChronoField.MONTH_OF_YEAR], dateTemporal[ChronoField.DAY_OF_MONTH])
    }

    /** @param dateString e.g. "24/12/2025" */
    fun parseEuropeDate(dateString: String): LocalDate = parseAnyDate(dateString, parseEuropeDate)

    fun parseAnyDate(dateString: String, dateFormatter: DateTimeFormatter): LocalDate {
        val dateTemporal = dateFormatter.parse(dateString)
        return LocalDate.of(
            dateTemporal[ChronoField.YEAR],
            dateTemporal[ChronoField.MONTH_OF_YEAR],
            dateTemporal[ChronoField.DAY_OF_MONTH]
        )
    }

    /**
     * By default using the HTML &mdash; as a separator.
     * @param timeString e.g. "13:14—15:16"
     */
    fun parseTimes(timeString: String, timeSeparator: String = "—"): TimeRange =
        timeString.split(timeSeparator).map { twoTimes ->
            twoTimes.trim().split(":").let { numberParts ->
                require(numberParts.size == 2) { "Expected to be 2 number parts but were ${numberParts.size} for time string: [$timeString]" }
                numberParts[0].toInt() to numberParts[1].toInt()
            }
        }.let { twoTimesList ->
            require(twoTimesList.size == 2) { "Times list expected to be 2 but was ${twoTimesList.size} for time string: [$timeString]" }
            TimeRange(twoTimesList[0].toLocalTime(), twoTimesList[1].toLocalTime())
        }

    /**
     * @param dateString e.g. "Friday, 27 December | 10:00 —11:15"
     */
    fun parseDateTimeRange(dateString: String, year: Int): DateTimeRange {
        val (datePart, timePart) = dateString.split("|").also {
            require(it.size == 2) { "Invalid elements, expected size 2: $it" }
        }.let { it[0].trim() to it[1].trim() }
        val date = parseDate(datePart, year)
        val times = parseTimes(timePart)
        return DateTimeRange(
            from = LocalDateTime.of(date, times.start),
            to = LocalDateTime.of(date, times.end),
        )
    }

    /** @param time e.g. "3:15" or "18:58" */
    fun parseTime(time: String): LocalTime = parseTimeOrNull(time) ?: error("Invalid time string [$time]")

    /** @param time e.g. "3:15" or "18:58" */
    fun parseTimeOrNull(time: String): LocalTime? =
        try {
            LocalTime.from(timeParser.parse(time))
        } catch (_: DateTimeParseException) {
            null
        }
}

private fun Pair<Int, Int>.toLocalTime() = LocalTime.of(first, second)

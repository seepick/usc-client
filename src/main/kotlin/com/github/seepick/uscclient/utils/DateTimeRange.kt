package com.github.seepick.uscclient.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// java's Duration is not sufficient
public data class TimeRange(
    val start: LocalTime,
    val end: LocalTime,
) {
    init {
        require(start <= end) { "Start ($start) must be <= end ($end)" }
    }

    companion object // for extensions
}

// java's Period is not sufficient
public data class DateTimeRange(
    val from: LocalDateTime,
    val to: LocalDateTime,
) : Comparable<DateTimeRange> {
    init {
        require(from <= to) { "From ($from) must be <= to ($to)" }
    }

    companion object {
        fun merge(date: LocalDate, times: TimeRange) = DateTimeRange(
            from = LocalDateTime.of(date, times.start),
            to = LocalDateTime.of(date, times.end)
        )
    }

    private val fromDate = from.toLocalDate()
    private val fromTime = from.toLocalTime()

    fun isStartMatching(date: LocalDate, matchFrom: LocalTime? = null, matchTo: LocalTime? = null): Boolean {
        val dateMatches = fromDate.isEqual(date)
        if (matchFrom != null && matchTo != null) return dateMatches && fromTime >= matchFrom && fromTime <= matchTo
        if (matchFrom != null) return dateMatches && fromTime >= matchFrom
        if (matchTo != null) return dateMatches && fromTime <= matchTo
        return dateMatches
    }

    override fun compareTo(other: DateTimeRange): Int {
        val from = this.from.compareTo(other.from)
        return if (from != 0) from else this.to.compareTo(other.to)
    }

    fun minusOneYear() = DateTimeRange(
        from = from.minusYears(1),
        to = to.minusYears(1),
    )
}

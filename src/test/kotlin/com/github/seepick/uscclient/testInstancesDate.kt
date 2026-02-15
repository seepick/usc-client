package com.github.seepick.uscclient

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.long
import java.time.LocalTime

@Deprecated("use usc-client")
fun Arb.Companion.dateTimeRange() = arbitrary {
    val from = localDateTime().bind()
    DateTimeRange(
        from = from,
        to = from.plusMinutes(long(min = 30, max = 120).bind()),
    )
}

fun Arb.Companion.timeRange() = arbitrary {
    val start = LocalTime.of(int(min = 0, max = 20).bind(), int(min = 0, max = 59).bind())
    TimeRange(
        start = start,
        end = start.plusMinutes(long(min = 10, max = 180).bind())
    )
}

package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.utils.DateTimeRange
import com.github.seepick.uscclient.utils.TimeRange
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import java.net.URL
import java.time.LocalTime

internal fun Arb.Companion.phpSessionId() = arbitrary {
    PhpSessionId(value = string().bind())
}

fun Arb.Companion.url() = arbitrary {
    URL(
        "https://${
            string(
                minSize = 5,
                maxSize = 15,
                codepoints = Codepoint.alphanumeric()
            ).bind()
        }.${string(minSize = 3, maxSize = 3, codepoints = Codepoint.az()).bind()}"
    )
}

fun Arb.Companion.imageUrl() = arbitrary {
    val fileName =
        string(minSize = 3, maxSize = 15, codepoints = Codepoint.az()).bind() + ".png"
    URL("${url().bind()}/${fileName}")
}

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

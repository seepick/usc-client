package com.github.seepick.uscclient.thirdparty

import com.github.seepick.uscclient.shared.DateTimeRange

public data class DnysEvent(
    val title: String,
    val teacher: String,
    val dateTimeRange: DateTimeRange,
)

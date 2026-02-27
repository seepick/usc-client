package com.github.seepick.uscclient.shared

import java.time.Duration
import java.time.LocalDate

fun LocalDate.daysBetween(other: LocalDate): Long =
    Duration.between(atStartOfDay(), other.atStartOfDay()).toDays()

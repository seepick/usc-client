package com.github.seepick.uscclient.utils

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateParserTest : DescribeSpec() {
    init {
        describe("parseTime") {
            it("successfully") {
                DateParser.parseTimes("13:14—15:16") shouldBe TimeRange(
                    start = LocalTime.of(13, 14),
                    end = LocalTime.of(15, 16),
                )
                DateParser.parseTimes("03:04—05:06") shouldBe TimeRange(
                    start = LocalTime.of(3, 4),
                    end = LocalTime.of(5, 6),
                )
            }
        }
        describe("parseDate") {
            it("successfully") {
                DateParser.parseDate("Monday, 9 December", 2024) shouldBe
                        LocalDate.of(2024, 12, 9)
                DateParser.parseDate("Tuesday, 24 December", 2024) shouldBe
                        LocalDate.of(2024, 12, 24)
            }
        }
        describe("parseDateTimeRange") {
            it("successfully") {
                DateParser.parseDateTimeRange(
                    "Monday, 9 December | 03:04 —05:06", 2024
                ) shouldBe DateTimeRange(
                    from = LocalDateTime.of(2024, 12, 9, 3, 4),
                    to = LocalDateTime.of(2024, 12, 9, 5, 6),
                )
                DateParser.parseDateTimeRange(
                    "Friday, 27 December | 10:00 —11:15", 2024
                ) shouldBe DateTimeRange(
                    from = LocalDateTime.of(2024, 12, 27, 10, 0),
                    to = LocalDateTime.of(2024, 12, 27, 11, 15),
                )
            }
        }
    }
}

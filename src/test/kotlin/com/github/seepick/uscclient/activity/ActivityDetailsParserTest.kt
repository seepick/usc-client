package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.utils.DateTimeRange
import com.github.seepick.uscclient.plan.Plan.UscPlan
import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime

class ActivityDetailsParserTest : StringSpec() {

    private val anyYear = 2000

    init {
        "Extract date time" {
            extractDateTime("If you need to cancel, please cancel your booking by 27/12/2024, 08:00.") shouldBe
                    LocalDateTime.of(2024, 12, 27, 8, 0)
        }
        "When parse upcoming Then return" {
            ActivityDetailsParser.parseDetails(readTestResponse("activity_detail.html"), 2024) shouldBe ActivityDetails(
                name = "RESTORATIVE YOGA",
                dateTimeRange = DateTimeRange(
                    from = LocalDateTime.of(2024, 12, 27, 10, 0),
                    to = LocalDateTime.of(2024, 12, 27, 11, 15)
                ),
                venueName = "Movements City",
                category = "Yoga",
                spotsLeft = 2,
                cancellationDateLimit = LocalDateTime.of(2024, 12, 27, 8, 0),
                plan = UscPlan.Medium,
                teacher = "Teacher A.",
                description = "activity details",
            )
        }
        "When parse old Then return" {
            ActivityDetailsParser.parseDetails(
                readTestResponse("activity_detail.past.html"),
                2024
            ) shouldBe ActivityDetails(
                name = "Sound Healing with Katty",
                dateTimeRange = DateTimeRange(
                    from = LocalDateTime.of(2024, 12, 24, 15, 0),
                    to = LocalDateTime.of(2024, 12, 24, 16, 0)
                ),
                venueName = "Yogaspot Olympisch Stadion",
                category = "Meditation",
                spotsLeft = 0, // for past, this is actually not available ;)
                cancellationDateLimit = null,
                plan = UscPlan.Small,
                teacher = "Teacher P.",
                description = "past description",
            )
        }
        "When parse single freetraining Then return" {
            ActivityDetailsParser.parseFreetraining(
                readTestResponse("activity_detail.freetraining.html"),
                2024
            ) shouldBe FreetrainingDetails(
                id = 83664090,
                name = "Wellness Spa",
                date = LocalDate.of(2024, 12, 29),
                venueSlug = "vitality-spa-fitness-amsterdam",
                category = "Wellness",
                plan = UscPlan.Small,
            )
        }

        "When parse without teacher Then ignore it" {
            ActivityDetailsParser.parseDetails(
                readTestResponse("activity_detail.teacher_absent.html"),
                anyYear
            ).also {
                it.teacher.shouldBeNull()
            }
        }
    }
}

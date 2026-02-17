package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.readTestResponse
import com.github.seepick.uscclient.utils.DateTimeRange
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ActivitiesParserTest : StringSpec() {

    private fun readActivitiesJson(date: LocalDate): List<ActivityInfo> =
        ActivitiesParser.parseContent(readTestResponse<ActivitiesJson>("activities.single.json").data.content, date)

    init {
        "parse single" {
            val date = LocalDate.of(2024, 10, 22)

            val activityInfos = readActivitiesJson(date)

            activityInfos.shouldBeSingleton().first() shouldBe ActivityInfo(
                id = 74626938,
                name = "Kickboks zaktraining",
                venueSlug = "basecampwest",
                category = "Mixed Martial Arts",
                spotsLeft = 7,
                plan = Plan.UscPlan.Medium,
                dateTimeRange = DateTimeRange(
                    from = LocalDateTime.of(date, LocalTime.of(7, 0)),
                    to = LocalDateTime.of(date, LocalTime.of(7, 45)),
                ),
            )
        }
        "parse single freetraining" {
            val singleFreetrainingHtml = readTestResponse<String>("activities.freetraining-single.html")

            val freetrainingInfos = ActivitiesParser.parseFreetrainingContent(singleFreetrainingHtml)

            freetrainingInfos.shouldBeSingleton()
                .first() shouldBe FreetrainingInfo(
                id = 83845951,
                name = "Aerial",
                category = "Aerial",
                venueSlug = "aerials-amsterdam-cla",
                plan = Plan.UscPlan.Medium,
            )
        }
        "parse freetrainings" {
            val htmlString = readTestResponse<ActivitiesJson>("activities.freetraining.json").data.content

            val result = ActivitiesParser.parseFreetrainingContent(htmlString)

            result.shouldHaveSize(25)
            result[12] shouldBe FreetrainingInfo(
                id = 83846191,
                name = "Essentrics",
                category = "Fitness",
                venueSlug = "calisthenics-amsterdam-rembrandtpark",
                plan = Plan.UscPlan.Small,
            )
        }
    }
}

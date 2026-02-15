package seepick.localsportsclub.api.checkin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import seepick.localsportsclub.readTestResponse
import seepick.localsportsclub.service.date.TimeRange
import java.time.LocalDate

class CheckinsParserTest : StringSpec() {
    private val year = 2024
    private val today = LocalDate.of(year, 12, 30)

    private fun parseCheckinsFile(fileName: String, date: LocalDate = today) =
        CheckinsParser.parse(readTestResponse<String>(fileName), date)

    init {
        "When parse empty Then return empty" {
            val result = parseCheckinsFile("checkins.empty.html")

            result.entries.shouldBeEmpty()
        }
        "When parse some Then return them" {
            val result = parseCheckinsFile("checkins.html")

            result.entries shouldBe listOf(
                ActivityCheckinEntry(
                    activityId = 84726253,
                    venueSlug = "yoga-spot-olympisch-stadion",
                    date = LocalDate.of(year, 12, 24),
                    timeRange = TimeRange("15:00-16:00"),
                    type = ActivityCheckinEntryType.Checkedin,
                ),
                ActivityCheckinEntry(
                    activityId = 84742854,
                    venueSlug = "studio-108-3",
                    date = LocalDate.of(year, 12, 24),
                    timeRange = TimeRange("10:00-11:15"),
                    type = ActivityCheckinEntryType.Checkedin,
                ),
                ActivityCheckinEntry(
                    activityId = 83535971,
                    venueSlug = "de-nieuwe-yogaschool",
                    date = LocalDate.of(year, 12, 23),
                    timeRange = TimeRange("15:45-17:00"),
                    type = ActivityCheckinEntryType.Checkedin,
                ),
            )
        }
        "When parse some in the future Then return them" {
            val result = parseCheckinsFile("checkins.html", today.withMonth(1))

            result.entries.forEach { it.date.year shouldBe (today.year - 1) }
        }
        "When parse with activity and freetraining Then return both" {
            val result = parseCheckinsFile("checkins.withFreetraining.html")

            result.entries shouldBe listOf(
                FreetrainingCheckinEntry(
                    freetrainingId = 83664090,
                    venueSlug = "vitality-spa-fitness-amsterdam",
                    date = LocalDate.of(year, 12, 29),
                ),
                ActivityCheckinEntry(
                    activityId = 84865371,
                    venueSlug = "movements-city",
                    date = LocalDate.of(year, 12, 29),
                    timeRange = TimeRange("9:00-10:00"),
                    type = ActivityCheckinEntryType.Checkedin,
                ),
            )
        }
        "When parse with no-show activity Then mark it" {
            val result = parseCheckinsFile("checkins.noshow.html")

            result.entries.shouldBeSingleton().first()
                .shouldBeInstanceOf<ActivityCheckinEntry>().type shouldBe ActivityCheckinEntryType.Noshow
        }

        "When parse late cancellation Then return proper state" {
            val result = parseCheckinsFile("checkins.cancellate.html")

            result.entries.shouldBeSingleton().first()
                .shouldBeInstanceOf<ActivityCheckinEntry>().type shouldBe ActivityCheckinEntryType.CancelledLate
        }
    }
}

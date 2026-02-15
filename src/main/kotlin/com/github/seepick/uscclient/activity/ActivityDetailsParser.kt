package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.DateParser
import com.github.seepick.uscclient.serializerLenient
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDateTime

private val regexpDate = Regex("""\d\d/\d\d/\d\d\d\d""")
private val regexpTime = Regex("""\d\d:\d\d""")

fun extractDateTime(string: String): LocalDateTime {
    val dateString = regexpDate.find(string)?.value ?: error("Couldn't find date in string [$string]")
    val timeString = regexpTime.find(string)?.value ?: error("Couldn't find time in string [$string]")
    return LocalDateTime.of(DateParser.parseEuropeDate(dateString), DateParser.parseTime(timeString))
}

object ActivityDetailsParser {

    fun parseDetails(html: String, currentYear: Int): ActivityDetails {
        val document = Jsoup.parse(html)
        val root = document.childNodes()[0] as Element
        val body = root.children()[1]
        val div = body.children().first()!!

        val dateString = div.select("p.smm-class-details__datetime").text()
        val dateRange = DateParser.parseDateTimeRange(dateString, currentYear)
        val buttonBook = div.select("button.book")
        val cancellationDateLimit = div.select("p.smm-class-details__hint.cancellation.requested").text().trim().let {
            if (it.isEmpty()) null else extractDateTime(it)
        }
        val plan = div.selectPlanFromDetail()
        val teacher = div.select("span.teacher").parents().first()?.text()?.trim()
        val description = div.select("span.class-description").text().trim()
        return if (buttonBook.hasAttr("data-book-success")) {
            val json = buttonBook.attr("data-book-success")
            val data = serializerLenient.decodeFromString<ActivityBookDataJson>(json)
            ActivityDetails(
                name = cleanActivityFreetrainingName(data.`class`.name),
                dateTimeRange = dateRange,
                venueName = data.venue.name.trim(),
                category = data.`class`.category.trim(),
                spotsLeft = data.`class`.spots_left.toInt(),
                cancellationDateLimit = cancellationDateLimit,
                plan = plan,
                teacher = teacher,
                description = description,
            )
        } else {
            val buttonCancel = div.select("button.cancel")
            val json = buttonCancel.attr("data-book-cancel")
            val data = serializerLenient.decodeFromString<ActivityCancelDataJson>(json)
            ActivityDetails(
                name = cleanActivityFreetrainingName(data.`class`.name),
                dateTimeRange = dateRange,
                venueName = data.venue.name.trim(),
                category = data.`class`.category.trim(),
                spotsLeft = 0,
                cancellationDateLimit = cancellationDateLimit,
                plan = plan,
                teacher = teacher,
                description = description,
            )
        }
    }

    fun parseFreetraining(html: String, year: Int): FreetrainingDetails {
        val document = Jsoup.parse(html)
        val root = document.childNodes()[0] as Element
        val body = root.children()[1]
        val div = body.children().first()!!
        val plan = div.selectPlanFromDetail()
        return FreetrainingDetails(
            id = div.attr("data-appointment-id").toInt(),
            name = cleanActivityFreetrainingName(div.select("div.general h3").first()!!.text()),
            date = div.select("p.smm-class-details__datetime").text().let { DateParser.parseDate(it, year) },
            category = div.select("span.disciplines").parents().first()!!.text().trim(),
            venueSlug = parseSlugFromGoogleMapUrls(div.select("div.usc-google-map").attr("data-static-map-urls")),
            plan = plan,
        )
    }

    private fun parseSlugFromGoogleMapUrls(jsonString: String): String {
        val url = serializerLenient.decodeFromString<List<GoogleMapUrl>>(jsonString).first().url
        val fileName = url.replace("%2F", "/").substringAfterLast("/").substringBeforeLast("?")
        // staticMapMedium_1280x1280_amsterdam_13834_vitality-spa-fitness-amsterdam_172647253741728.png
        val parts = fileName.split("_")
        // [staticMapMedium, 1280x1280, amsterdam, 13834, vitality-spa-fitness-amsterdam, 172647253741728.png]
        return parts[4]
    }
}

@Serializable
private data class ActivityBookDataJson(
    val `class`: ActivityDataLayerClassJson,
    val venue: ActivityDataLayerVenueJson,
)

@Serializable
private data class ActivityCancelDataJson(
    val `class`: ActivityDataLayerCancelClassJson,
    val venue: ActivityDataLayerVenueJson,
)

@Serializable
private data class ActivityDataLayerVenueJson(
    val id: Int, // that's the USC internal ID, don't use it (not stable anyway)
    val name: String,
)

@Serializable
private data class ActivityDataLayerCancelClassJson(
    val id: String,
    val name: String,
    val category: String,
)

/**
 * {"width":1280,"url":"https://storage.googleapis.com/download/storage/v1/b/usc-pro-uscweb-live-media/o/de-live%2FstaticMapMedium_1280x1280_amsterdam_13834_vitality-spa-fitness-amsterdam_172647253741728.png?generation=1726472537636021&alt=media"},
 * {"width":640,"url":"https://storage.googleapis.com/download/storage/v1/b/usc-pro-uscweb-live-media/o/de-live%2FstaticMapSmall_640x640_amsterdam_13834_vitality-spa-fitness-amsterdam_172647253741728.png?generation=1726472537337492&alt=media"}
 */
@Serializable
private data class GoogleMapUrl(
    val width: Int,
    val url: String,
)

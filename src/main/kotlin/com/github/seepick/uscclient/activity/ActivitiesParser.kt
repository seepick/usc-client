package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.DateParser
import com.github.seepick.uscclient.DateTimeRange
import com.github.seepick.uscclient.Plan
import com.github.seepick.uscclient.serializerLenient
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate

object ActivitiesParser {
    private val log = KotlinLogging.logger {}

    fun parseContent(htmlString: String, date: LocalDate): List<ActivityInfo> {
        val document = Jsoup.parse(htmlString)
        val html = document.childNodes()[0] as Element
        val body = html.children()[1]
        val divs = body.children()
        log.debug { "Parsing ${divs.size} activities." }
        return divs.map { div ->
            parseSingleActivity(div, date)
        }
    }

    fun parseFreetrainingContent(htmlString: String): List<FreetrainingInfo> {
        val document = Jsoup.parse(htmlString)
        val html = document.childNodes()[0] as Element
        val body = html.children()[1]
        val divs = body.children()
        log.debug { "Parsing ${divs.size} freetrainings." }
        return divs.map(::parseSingleFreetraining)
    }

    private fun parseSingleFreetraining(div: Element): FreetrainingInfo =
        FreetrainingInfo(
            id = div.attr("data-appointment-id").toInt(),
            plan = div.selectPlanFromSnippet(),
            name = cleanActivityFreetrainingName(div.select("div.title a.title").text()),
            category = div.select("div.title p").text().trim(),
            venueSlug = div.select("a.smm-studio-link").first()!!.attr("href").substringAfterLast("/").trim(),
        )

    private fun parseSingleActivity(div: Element, date: LocalDate): ActivityInfo {
        val dataLayerJsonString = div.select("a[href=\"#modal-class\"]").first()!!.attr("data-datalayer")
        val dataLayer = serializerLenient.decodeFromString<ActivityDataLayerJson>(dataLayerJsonString).`class`
        val dateTimeRange = DateTimeRange.merge(
            date, DateParser.parseTimes(div.select("p.smm-class-snippet__class-time").text())
        )
        require(
            div.attr("data-appointment-id").toInt() == dataLayer.id.toInt()
        ) { "IDs expected to be identical but weren't!" }
        return ActivityInfo(
            id = dataLayer.id.toInt(),
            name = cleanActivityFreetrainingName(dataLayer.name),
            venueSlug = div.select("a.smm-studio-link").first()!!.attr("href").substringAfterLast("/").trim(),
            dateTimeRange = dateTimeRange,
            category = dataLayer.category.trim(),
            plan = div.selectPlanFromSnippet(),
            spotsLeft = dataLayer.spots_left.toInt(),
        )
    }

}

data class ActivityInfo(
    val id: Int,
    val name: String,
    val venueSlug: String,
    val dateTimeRange: DateTimeRange,
    val category: String, // aka disciplines/facilities
    val spotsLeft: Int,
    val plan: Plan.UscPlan,
    // type = "instant booking"
)

data class FreetrainingInfo(
    val id: Int,
    val name: String,
    val category: String,
    val venueSlug: String,
    val plan: Plan.UscPlan,
)

@Serializable
private data class ActivityDataLayerJson(
    // event: String,
    // user: ...,
    val `class`: ActivityDataLayerClassJson,
)

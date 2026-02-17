package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.shared.jsonSerializer
import com.github.seepick.uscclient.utils.DateParser
import com.github.seepick.uscclient.utils.DateTimeRange
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate

public fun cleanActivityFreetrainingName(input: String): String {
    var htmlInput = input
    var oldHtmlInput: String
    do {
        oldHtmlInput = htmlInput
        htmlInput = Jsoup.parse(oldHtmlInput).text()
    } while (oldHtmlInput != htmlInput)
    return htmlInput.trim()
}

internal object ActivitiesParser {

    private val log = logger {}

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
        val dataLayer = jsonSerializer.decodeFromString<ActivityDataLayerJson>(dataLayerJsonString).`class`
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

internal fun Element.selectPlanFromSnippet(): Plan.UscPlan =
    selectPlanForClassFrom("snippet")

internal fun Element.selectPlanFromDetail(): Plan.UscPlan =
    selectPlanForClassFrom("details")

private fun Element.selectPlanForClassFrom(infix: String): Plan.UscPlan =
    selectPlanFrom("smm-class-${infix}__class-plan")

internal fun Element.selectPlanForVenue(): Plan.UscPlan =
    selectPlanFrom("smm-studio-snippet__studio-plan")

private fun Element.selectPlanFrom(queryName: String): Plan.UscPlan =
    select(".${queryName}s > .${queryName}").map {
        Plan.UscPlan.byId(it.attr("data-type").toInt())
    }.minBy { it.id }

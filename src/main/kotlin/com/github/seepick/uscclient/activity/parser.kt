package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.Plan
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun Element.selectPlanFromSnippet(): Plan.UscPlan =
    selectPlanForClassFrom("snippet")

fun Element.selectPlanFromDetail(): Plan.UscPlan =
    selectPlanForClassFrom("details")

private fun Element.selectPlanForClassFrom(infix: String): Plan.UscPlan =
    selectPlanFrom("smm-class-${infix}__class-plan")

fun Element.selectPlanForVenue(): Plan.UscPlan =
    selectPlanFrom("smm-studio-snippet__studio-plan")

private fun Element.selectPlanFrom(queryName: String): Plan.UscPlan =
    select(".${queryName}s > .${queryName}").map {
        Plan.UscPlan.byId(it.attr("data-type").toInt())
    }.minBy { it.id }

fun cleanActivityFreetrainingName(input: String): String {
    var htmlInput = input
    var oldHtmlInput: String
    do {
        oldHtmlInput = htmlInput
        htmlInput = Jsoup.parse(oldHtmlInput).text()
    } while (oldHtmlInput != htmlInput)
    return htmlInput.trim()
}

@Serializable
data class ActivityDataLayerClassJson(
    val id: String,
    val name: String,
    val category: String,
    val spots_left: String,
)

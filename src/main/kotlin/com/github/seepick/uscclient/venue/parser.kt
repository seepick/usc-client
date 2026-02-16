package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.activity.selectPlanForVenue
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URL

internal object VenueParser {
    private val log = logger {}
    private const val NO_IMAGE_SET = "/images/merchant/venueCatalog.jpg"

    fun parseHtmlContent(htmlString: String): List<VenueInfo> {
        val document = Jsoup.parse(htmlString)
        val html = document.childNodes()[0] as Element
        val body = html.children()[1]
        val children = body.children()
        log.debug { "Parsing ${children.size} venues." }
        return children.map { div ->
            VenueInfo(
                title = div.select("p.smm-studio-snippet__title").text().trim(),
                slug = div.select("a.smm-studio-snippet__studio-link").attr("href").substringAfterLast("/"),
                imageUrl = div.select("img.smm-studio-snippet__lazy-image").attr("data-src").let {
                    if (it == NO_IMAGE_SET) null else URL(it)
                },
                disciplines = div.select("div.disciplines").text().trim().split("·").map { it.trim() },
                addressId = div.attr("data-address-id").toInt(),
                addressDistrict = div.select("p.smm-studio-snippet__address").text().trim().substringBefore(","),
                addressStreet = div.select("span.smm-studio-snippet__address-street").text().trim(),
                plan = div.selectPlanForVenue(),
            )
        }
    }
}

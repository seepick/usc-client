package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.utils.unescape
import com.github.seepick.uscclient.shared.JsoupUtil
import com.github.seepick.uscclient.shared.jsonSerializer
import java.net.URL

internal object VenueDetailsParser {

    private object EnglishLabels {
        const val OTHER_LOCATIONS = "Other Locations:"
        const val WEBSITE = "Website:"
        const val IMPORTANT_INFO = "Important Info:"
        const val OPENING_TIMES = "Opening Times:"
    }

    private val importantInfoDefaults = listOf(".", "-")
    private const val OPENING_TIMES_DEFAULT_VALUE_NL =
        "De openingstijden zijn afhankelijk van de cursustijden/afspraken, of zijn niet bekend. Je kunt meer informatie vinden op de partnerwebsite."
    private const val OPENING_TIMES_DEFAULT_VALUE_EN =
        "The opening hours depend on the course times / agreed appointments or are not known. You can find more information on the partner website."
    private const val NO_IMAGE_SET_URL = "https://urbansportsclub.com/images/merchant/venueHome.jpg"

    fun parse(htmlString: String): VenueDetails {
        val (head, body) = JsoupUtil.extractHeadAndBody(htmlString)

        val title = body.select("div#studio-info-container h1").text()
        val linkedVenues = mutableListOf<String>()
        var website: String? = null
        var openingTimes: String? = null
        var importantInfo: String? = null
        val json = body.select("script[type=\"application/ld+json\"]").first()!!.dataNodes().first().wholeData
        val detail = jsonSerializer.decodeFromString<VenueDetailEmbedJson>(json)
        val slug = head.select("meta[property=\"og:url\"]").attr("content").substringAfterLast("/")
        val disciplines = body.select("div.disciplines").text().split(",").map { it.trim() }
        val description = body.select("p.description").text()
        body.select("div.studio-info-section").forEach { div ->
            when (div.select("h2").single().text()) {
                EnglishLabels.OTHER_LOCATIONS -> {
                    div.select("a").forEach { a ->
                        linkedVenues += a.attr("href").substringAfterLast("/")
                    }
                }

                EnglishLabels.WEBSITE -> {
                    website = div.select("a").first()!!.attr("href")
                }

                EnglishLabels.IMPORTANT_INFO -> {
                    importantInfo = div.select("p span.pre-line").text().trim().let {
                        if (importantInfoDefaults.contains(it)) null else it
                    }
                }

                EnglishLabels.OPENING_TIMES -> {
                    openingTimes = div.select("p span.pre-line").text().trim().let {
                        if (it == OPENING_TIMES_DEFAULT_VALUE_EN || it == OPENING_TIMES_DEFAULT_VALUE_NL) null else it
                    }
                }
            }
        }
        return VenueDetails(
            title = title,
            slug = slug,
            linkedVenueSlugs = linkedVenues,
            websiteUrl = website?.let { URL(it) },
            disciplines = disciplines,
            description = description.unescape(),
            importantInfo = importantInfo?.unescape()?.let { cleanVenueInfo(it) },
            openingTimes = openingTimes?.unescape(),
            originalImageUrl = detail.image.let { if (it == NO_IMAGE_SET_URL) null else URL(it) },
            latitude = detail.geo.latitude,
            longitude = detail.geo.longitude,
            streetAddress = detail.address.streetAddress,
            addressLocality = detail.address.addressLocality,
            postalCode = detail.address.postalCode,
        )
    }
}

private val venueInfoMisbeginnings = listOf(".let op!", ".let op:", "let op:", ".note:", "note:", ".", "/")
fun cleanVenueInfo(input: String): String? {
    val prefix = venueInfoMisbeginnings.firstOrNull { input.startsWith(it, true) } ?: return input
    return input.substring(prefix.length).trim().let {
        it.ifEmpty { null }
    }
}

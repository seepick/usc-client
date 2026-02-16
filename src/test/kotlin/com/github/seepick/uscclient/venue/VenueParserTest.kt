package com.github.seepick.uscclient.venue

import com.github.seepick.uscclient.plan.Plan
import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.net.URL


class VenueParserTest : StringSpec() {
    private fun read(fileName: String): List<VenueInfo> {
        return VenueParser.parseHtmlContent(readTestResponse<VenuesJson>(fileName).data.content)
    }

    init {
        "When parse simplified venues Then parse data from HTML" {
            val result = read("venues.simplified.json")

            result.shouldBeSingleton().first() shouldBe VenueInfo(
                addressId = 25678,
                slug = "amsterdam-west",
                imageUrl = URL("https://storage.googleapis.com/download/storage/v1/b/usc-pro-uscweb-live-media/o/de-live%2FvenueCatalog_311x175_bmrtjlmxbveot0zlvwuj_1727358338834864.png?generation=1727358339345039&alt=media"),
                title = "24Seven - Amsterdam West",
                disciplines = listOf("Bokssport", "Fitness"),
                addressDistrict = "West",
                addressStreet = "Herentalsstraat 132",
                plan = Plan.UscPlan.Medium,
            )
        }
        "When merchant venue catalog image is set Then parse null" {
            val result = read("venues.noImage.json")
            result.shouldBeSingleton().first().imageUrl.shouldBeNull()
        }
        "When fetch DE Then parse" {
            val result = read("venues.de.json")
            println(result)
        }
    }
}

package seepick.localsportsclub.api.venue

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.http.Url
import seepick.localsportsclub.readTestResponse

class VenueDetailsParserTest : DescribeSpec() {
    init {
        fun readAndParse(fileName: String) =
            VenueDetailsParser.parse(readTestResponse(fileName))

        fun read0() = readAndParse("venue_detail-linked0.html")
        fun read1() = readAndParse("venue_detail-linked1.html")
        fun readn() = readAndParse("venue_detail-linkedn.html")
        fun readH3() = readAndParse("venue_detail-h3_to_h2.html")

        describe("Title") {
            it("simple") {
                read0().title shouldBe "Test Venue Title"
            }
            it("without username") {
                val detail = readAndParse("venue_detail-title_parse.html")
                detail.title shouldBe "Trainmore Beethovenstraat"
            }
        }
        describe("Misc") {
            it("slug") {
                read0().slug shouldBe "aerials-amsterdam-cla"
            }
            it("disciplines") {
                read0().disciplines shouldBe listOf("Aerial", "Yoga")
            }
            it("JSON embedded") {
                read0().should {
                    it.latitude shouldBe "52.357123"
                    it.longitude shouldBe "4.8385649"
                    it.addressLocality shouldBe "Amsterdam, Netherlands"
                    it.streetAddress shouldBe "Main Street 42"
                    it.postalCode shouldBe "1001 AA"
                }
            }
        }
        describe("Linked partner venues") {
            it("Given no linked venue and no website") {
                read0().linkedVenueSlugs shouldBe emptyList()
            }
            it("Given one linked venue and website") {
                read1().linkedVenueSlugs shouldBe listOf("aerials-amsterdam-lola")
            }
            it("Given multiple linked venues and no website") {
                readn().linkedVenueSlugs shouldBe
                        listOf(
                            "amsterdam-noord-binnen",
                            "amsterdam-noord",
                            "amsterdam-venserpolder",
                            "amsterdam-zuidoost"
                        )
            }
        }
        describe("Website URL") {
            it("Not set") {
                read0().websiteUrl.shouldBeNull()
            }
            it("Set") {
                read1().websiteUrl shouldBe Url("https://www.aerials.amsterdam")
            }
        }
        describe("Opening Times") {
            it("When set Then read") {
                read0().openingTimes shouldBe "test opening"
            }
            it("When english default Then nullified") {
                read1().openingTimes.shouldBeNull()
            }
            it("When dutch default Then nullified") {
                readn().openingTimes.shouldBeNull()
            }
        }
        describe("Description") {
            it("always set") {
                read0().description shouldBe "test description"
            }
        }
        describe("Important info") {
            it("When set Then read") {
                read0().importantInfo shouldBe "test important info"
            }
            it("When default Then nullify") {
                read1().importantInfo.shouldBeNull()
            }
        }
        describe("USC backend change") {
            it("studio-info-section sub elements changed from h3 to h2") {
                readH3().importantInfo shouldBe "movement important info"
            }
        }
    }
}

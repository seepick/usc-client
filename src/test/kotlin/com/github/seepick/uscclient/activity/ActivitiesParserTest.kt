package com.github.seepick.uscclient.activity

import com.github.seepick.uscclient.DateTimeRange
import com.github.seepick.uscclient.Plan
import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ActivitiesParserTest : StringSpec() {

    private val singleFreetrainingHtmlContent = """<div class="smm-class-snippet row"
   data-appointment-id="83845951"
   data-address-id="25091">
   <a href="#modal-class"
      data-href="/en/class-details/83845951"
      data-toggle="modal"
      data-datalayer="{&quot;event&quot;:&quot;page_viewed&quot;,&quot;user&quot;:{&quot;id&quot;:&quot;d424b2ddf89e71cea39e57e3f3a3d919&quot;,&quot;login_status&quot;:&quot;logged-in&quot;,&quot;membership_city&quot;:&quot;Amsterdam&quot;,&quot;membership_country&quot;:&quot;Netherlands&quot;,&quot;membership_status&quot;:&quot;active&quot;,&quot;membership_plan&quot;:&quot;L&quot;,&quot;membership_b2b_type&quot;:&quot;b2c&quot;,&quot;membership_contract_duration&quot;:&quot;monthly&quot;,&quot;company_name&quot;:null},&quot;class&quot;:{&quot;id&quot;:&quot;83845951&quot;,&quot;name&quot;:&quot;Aerial&quot;,&quot;category&quot;:&quot;Aerial&quot;,&quot;type&quot;:&quot;instant booking&quot;,&quot;spots_left&quot;:&quot;2147483647&quot;,&quot;search_position&quot;:1},&quot;type&quot;:&quot;class details&quot;,&quot;language&quot;:&quot;en&quot;,&quot;city&quot;:&quot;Amsterdam&quot;,&quot;country&quot;:&quot;Netherlands&quot;,&quot;environment&quot;:&quot;de-live&quot;,&quot;referrer&quot;:&quot;https:\/\/urbansportsclub.com\/en\/activities?city_id=1144\u0026date=2024-12-30\u0026plan_type=3&quot;}"
      class="smm-class-link image">
      <div class="smm-class-snippet__image"
         style="background-image: url('https://storage.googleapis.com/download/storage/v1/b/usc-pro-uscweb-live-media/o/de-live%2FvenueCatalogOneColumn_962x542_kdjwn8pmiweqb7m3h23f_1726486058950151.png?generation=1726486059615362&amp;alt=media')">
      </div>
   </a>
   <div class="time col-xs-3 col-md-2 col-md-offset-2">
      <div class="smm-class-snippet__class-time-plans-wrapper">
         <span class="smm-class-snippet__class-plans">
         <span class="smm-class-snippet__class-plan" data-type="2">
         Classic                                            </span>
         <span class="smm-class-snippet__class-plan" data-type="3">
         Premium                                            </span>
         <span class="smm-class-snippet__class-plan" data-type="6">
         Max                                            </span>
         </span>
         <div class="booking-state-label-wrapper">
            <span class="smm-booking-state-label label booking-state">
            </span>
         </div>
      </div>
   </div>
   <div class="title col-xs-offset-3 col-md-3 col-md-offset-0">
      <a href="#modal-class" data-href="/en/class-details/83845951" data-toggle="modal"
         class="smm-class-link title"
         data-datalayer="{&quot;event&quot;:&quot;page_viewed&quot;,&quot;user&quot;:{&quot;id&quot;:&quot;d424b2ddf89e71cea39e57e3f3a3d919&quot;,&quot;login_status&quot;:&quot;logged-in&quot;,&quot;membership_city&quot;:&quot;Amsterdam&quot;,&quot;membership_country&quot;:&quot;Netherlands&quot;,&quot;membership_status&quot;:&quot;active&quot;,&quot;membership_plan&quot;:&quot;L&quot;,&quot;membership_b2b_type&quot;:&quot;b2c&quot;,&quot;membership_contract_duration&quot;:&quot;monthly&quot;,&quot;company_name&quot;:null},&quot;class&quot;:{&quot;id&quot;:&quot;83845951&quot;,&quot;name&quot;:&quot;Aerial&quot;,&quot;category&quot;:&quot;Aerial&quot;,&quot;type&quot;:&quot;instant booking&quot;,&quot;spots_left&quot;:&quot;2147483647&quot;,&quot;search_position&quot;:1},&quot;type&quot;:&quot;class details&quot;,&quot;language&quot;:&quot;en&quot;,&quot;city&quot;:&quot;Amsterdam&quot;,&quot;country&quot;:&quot;Netherlands&quot;,&quot;environment&quot;:&quot;de-live&quot;,&quot;referrer&quot;:&quot;https:\/\/urbansportsclub.com\/en\/activities?city_id=1144\u0026date=2024-12-30\u0026plan_type=3&quot;}"
         >
      Aerial        </a>
      <p>Aerial</p>
   </div>
   <div
      class="address col-xs-offset-3 col-md-3 col-md-offset-0"
      >
      <span class="district">
      Nieuw-West        </span>
      <a class="smm-studio-link hidden-lg"
         target="_self"
         href="/en/venues/aerials-amsterdam-cla">
      <i class="fa fa-map-marker"></i>
      Aerials Amsterdam            </a>
      <a class="smm-studio-link visible-lg"
         target="_blank"
         href="/en/venues/aerials-amsterdam-cla">
      <i class="fa fa-map-marker"></i>
      Aerials Amsterdam            </a>
   </div>
   <div class="details col-xs-12 col-xs-offset-0 col-md-2 col-md-offset-0">
      <a href="#modal-class" data-href="/en/class-details/83845951" data-toggle="modal"
         data-datalayer="{&quot;event&quot;:&quot;page_viewed&quot;,&quot;user&quot;:{&quot;id&quot;:&quot;d424b2ddf89e71cea39e57e3f3a3d919&quot;,&quot;login_status&quot;:&quot;logged-in&quot;,&quot;membership_city&quot;:&quot;Amsterdam&quot;,&quot;membership_country&quot;:&quot;Netherlands&quot;,&quot;membership_status&quot;:&quot;active&quot;,&quot;membership_plan&quot;:&quot;L&quot;,&quot;membership_b2b_type&quot;:&quot;b2c&quot;,&quot;membership_contract_duration&quot;:&quot;monthly&quot;,&quot;company_name&quot;:null},&quot;class&quot;:{&quot;id&quot;:&quot;83845951&quot;,&quot;name&quot;:&quot;Aerial&quot;,&quot;category&quot;:&quot;Aerial&quot;,&quot;type&quot;:&quot;instant booking&quot;,&quot;spots_left&quot;:&quot;2147483647&quot;,&quot;search_position&quot;:1},&quot;type&quot;:&quot;class details&quot;,&quot;language&quot;:&quot;en&quot;,&quot;city&quot;:&quot;Amsterdam&quot;,&quot;country&quot;:&quot;Netherlands&quot;,&quot;environment&quot;:&quot;de-live&quot;,&quot;referrer&quot;:&quot;https:\/\/urbansportsclub.com\/en\/activities?city_id=1144\u0026date=2024-12-30\u0026plan_type=3&quot;}"
         class="smm-class-link usc-button-rebrand usc-button-rebrand--default button">
      Continue            </a>
   </div>
</div>"""

    private fun read(fileName: String, date: LocalDate): List<ActivityInfo> =
        ActivitiesParser.parseContent(readTestResponse<ActivitiesJson>(fileName).data.content, date)

    init {
        "parse single" {
            val date = LocalDate.of(2024, 10, 22)
            read("activities.single.json", date).shouldBeSingleton().first() shouldBe ActivityInfo(
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
            ActivitiesParser.parseFreetrainingContent(singleFreetrainingHtmlContent).shouldBeSingleton()
                .first() shouldBe FreetrainingInfo(
                id = 83845951,
                name = "Aerial",
                category = "Aerial",
                venueSlug = "aerials-amsterdam-cla",
                plan = Plan.UscPlan.Medium,
            )
        }
        "parse freetrainings" {
            val result =
                ActivitiesParser.parseFreetrainingContent(readTestResponse<ActivitiesJson>("activities.freetraining.json").data.content)
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

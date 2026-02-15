package com.github.seepick.uscclient.plan

import com.github.seepick.uscclient.City
import com.github.seepick.uscclient.Country
import com.github.seepick.uscclient.Plan
import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MembershipParserTest : StringSpec() {
    init {
        "When parse Then return plan type" {
            MembershipParser.parse(readTestResponse<String>("membership.html")) shouldBe Membership(
                plan = Plan.OnefitPlan.Premium,
                country = Country.byLabel("Netherlands"),
                city = City.byLabel("Amsterdam"),
            )
        }
    }
}

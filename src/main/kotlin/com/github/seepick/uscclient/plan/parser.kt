package com.github.seepick.uscclient.plan

import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.model.Country
import com.github.seepick.uscclient.shared.JsoupUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal object MembershipParser {
    fun parse(string: String): Membership {
        val body = JsoupUtil.extractBody(string)
        val membershipDiv = body.select("div[data-scroll=membership]").toList().first()
        val membershipJsonString = membershipDiv.select("a[class=form-link]").toList()
            .first().attr("data-datalayer")
        val membershipJson = Json.decodeFromString<MemberJson>(membershipJsonString)
        val plan = if (membershipJson.membership_plan == "Premium OneFit") {
            Plan.OnefitPlan.Premium
        } else {
            Plan.UscPlan.byApiString(membershipJson.user.membership_plan)
        }
        return Membership(
            plan = plan,
            city = City.byLabel(membershipJson.user.membership_city),
            country = Country.byLabel(membershipJson.user.membership_country),
        )
    }
}


/*
{
  "event": "membership_update_started",
  "user": {
    "id": "asdf",
    "login_status": "logged-in",
    "membership_city": "Amsterdam",
    "membership_country": "Netherlands",
    "membership_status": "active",
    "membership_plan": "L",
    "membership_b2b_type": "b2c",
    "membership_contract_duration": "monthly",
    "company_name": null
  },
  "type": "membership_plan",
  "membership_plan": "Premium OneFit",
  "membership_duration": "monthly"
}
 */

@Serializable
data class MemberJson(
    val event: String,
    val type: String,
    val membership_plan: String,
    val membership_duration: String,
    val user: MemberUserJson,
)

@Serializable
data class MemberUserJson(
    val id: String,
    val login_status: String, // logged-in
    val membership_city: String,
    val membership_country: String,
    val membership_status: String, // active
    val membership_plan: String, // L
    val membership_b2b_type: String, // b2c
    val membership_contract_duration: String, // monthly
    val company_name: String?,
)

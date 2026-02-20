@file:Suppress("unused")

package com.github.seepick.uscclient.plan

import com.github.seepick.uscclient.model.City
import com.github.seepick.uscclient.model.Country

public data class Membership(
    val plan: Plan,
    val country: Country,
    val city: City,
)

public sealed interface Plan {
    companion object {
        public fun byInternalId(internalId: String): Plan =
            UscPlan.entries.singleOrNull { it.internalId == internalId }
                ?: OnefitPlan.entries.singleOrNull { it.internalId == internalId }
                ?: error("Invalid internal plan ID: [${internalId}]!")
    }

    val id: Int // passed as URL query param
    val internalId: String
    val apiString: String // found in JSON
    val label: String
    val emoji: String
    val usageInfo: UsageInfo
    val fullLabel: String get() = "$emoji $label ($apiString)"

    public enum class UscPlan(
        override val id: Int,
        override val internalId: String,
        override val apiString: String,
        override val label: String,
        override val emoji: String,
        override val usageInfo: UsageInfo,
    ) : Plan {
        Small(
            id = 1,
            internalId = "uscSmall",
            apiString = "S",
            label = "Essential",
            emoji = "🥉",
            usageInfo = UsageInfo(
                maxCheckinsInPeriod = 4,
                maxOnlineCheckins = 4,
                maxPlusCheckins = 0,
                maxCheckinsInMonthPerVenue = 2,
            )
        ),
        Medium(
            id = 2,
            internalId = "uscMedium",
            apiString = "M",
            label = "Classic",
            emoji = "🥈",
            usageInfo = UsageInfo(
                maxCheckinsInPeriod = 10,
                maxOnlineCheckins = 8,
                maxPlusCheckins = 0,
                maxCheckinsInMonthPerVenue = 4,
            )
        ),
        Large(
            id = 3,
            internalId = "uscLarge",
            apiString = "L",
            label = "Premium",
            emoji = "🥇",
            usageInfo = UsageInfo(
                maxCheckinsInPeriod = 14,
                maxOnlineCheckins = 8,
                maxPlusCheckins = 4,
                maxCheckinsInMonthPerVenue = 6,
                // 2 per day
            )
        ),
        ExtraLarge(
            id = 6,
            internalId = "uscExtraLarge",
            apiString = "XL",
            label = "Max",
            emoji = "🏆",
            usageInfo = UsageInfo(
                maxCheckinsInPeriod = 18,
                maxOnlineCheckins = 8,
                maxPlusCheckins = 8,
                maxCheckinsInMonthPerVenue = 6,
            )
        );

        companion object {
            val default = Small
            val emoji = Large.emoji
            fun byApiString(apiString: String) = entries.single { it.apiString == apiString }
            fun byId(id: Int) = entries.single { it.id == id }
        }

        override fun toString() = "Plan.Usc.$name"
    }

    public enum class OnefitPlan(
        val uscPlan: UscPlan,
    ) : Plan by uscPlan {
        Premium(UscPlan.Large) {
            override val internalId = "onefitPremium"
            override val label = "Onefit Premium"
            override val usageInfo = UscPlan.Large.usageInfo.copy(
                maxCheckinsInPeriod = 18, // instead of only 14
            )
        };

        override fun toString() = "Plan.Onefit.$name"
    }
}

public data class UsageInfo(
    val maxCheckinsInPeriod: Int, // max monthly, real-life
    val maxOnlineCheckins: Int, // max monthly, internet videostream
    val maxPlusCheckins: Int,
    val maxCheckinsInMonthPerVenue: Int,
) {
    // "limits may differ per partner but average is around 6" - by USC support
    val maxReservationsPerVenue = 6 // valid for all plans
    val maxReservationsPerDay = 2 // valid for all plans
}

package com.github.seepick.uscclient.venue

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class VenueDetailsVisitLimitsParserTest : StringSpec({

    "missing entry defaults to 0" {
        parseVisitLimits(
            """
            M-members kunnen tot 4x per maand bij deze locatie inchecken
            L- &amp; XL- members kunnen tot 8x per maand bij deze locatie inchecken
        """.trimIndent()
        ) shouldBe VisitLimits(0, 4, 8, 8)
    }
    "ignore appendix" {
        parseVisitLimits(
            """
            S-members kunnen tot 2x per maand bij deze locatie inchecken
            M- members kunnen tot 4x per maand bij deze locatie inchecken
            L- &amp; XL- members kunnen tot 8x per maand bij deze locatie inchecken

            B2B members:
            M &amp; L &amp; XL-members kunnen tot 1x per dag bij deze locatie inchecken
        """.trimIndent()
        ) shouldBe VisitLimits(2, 4, 8, 8)
    }
    "daily" {
        parseVisitLimits(
            """
            L- members kunnen tot 8x per maand bij deze locatie inchecken.
            XL-members kunnen tot 1x per dag bij deze locatie inchecken.

            B2B:
            L- members kunnen tot 1x per dag bij deze locatie inchecken.
            XL-members kunnen tot 1x per dag bij deze locatie inchecken.
        """.trimIndent()
        ) shouldBe VisitLimits(0, 0, 8, 30)
    }
    "all" {
        parseVisitLimits(
            """
            Alle members kunnen tot 1x per maand bij deze locatie inchecken
        """.trimIndent()
        ) shouldBe VisitLimits(1, 1, 1, 1)
    }
    "b2c" {
        parseVisitLimits(
            """
            PILATES:
            B2C
            L &amp; XL-members kunnen tot 4x per maand bij deze locatie inchecken.
            B2B
            M, L &amp; XL-members kunnen tot 4x per maand bij deze locatie inchecken.
        """.trimIndent()
        ) shouldBe VisitLimits(0, 0, 4, 4)
    }
    "triple" {
        parseVisitLimits(
            """
            S-members kunnen tot 2x per maand voor online lessen inchecken
            M, L &amp; XL-members kunnen tot 8x per maand voor online lessen inchecken
        """.trimIndent()
        ) shouldBe VisitLimits(2, 8, 8, 8)
    }
    "autofix mistake" {
        parseVisitLimits(
            """
            S-members kunnen tot 2x per maand bij deze locatie inchecken
            M-members kunnen tot 4x per maand bij deze locatie inchecken
            M-members kunnen tot 6x per maand bij deze locatie inchecken
            XL-members kunnen tot 8x per maand bij deze locatie inchecken
        """.trimIndent()
        ) shouldBe VisitLimits(2, 4, 6, 8)
    }
})

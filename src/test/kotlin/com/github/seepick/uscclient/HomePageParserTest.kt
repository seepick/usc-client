package seepick.localsportsclub.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import seepick.localsportsclub.readTestResponse

const val responseHomeSecretKey = "UWZZNDJwNmEvaS9YTHZHN01XQ2QxQT09"
const val responseHomeSecretValue = "SlVBNHExWEQ4bncyQTZiRnBrcVNYQT09"

class HomePageParserTest : StringSpec() {
    init {
        "When parse home response Then extract login secret" {
            val result = HomePageParser.parse(readTestResponse("home.html"))

            result.loginSecret shouldBe (responseHomeSecretKey to responseHomeSecretValue)
        }
    }
}

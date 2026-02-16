package com.github.seepick.uscclient.login

import com.github.seepick.uscclient.readTestResponse
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

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

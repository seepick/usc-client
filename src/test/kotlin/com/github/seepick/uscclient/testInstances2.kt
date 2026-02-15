package com.github.seepick.uscclient

import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.ktor.http.Url

fun Arb.Companion.uscConfig() = arbitrary {
    UscConfig(
        baseUrl = url().bind(),
        storeResponses = boolean().bind(),
    )
}

fun Arb.Companion.city() = arbitrary {
    City(
        id = int().bind(),
        label = string().bind(),
    )
}

fun Arb.Companion.plan(): Arb<Plan> = arbitrary {
    if (boolean().bind()) {
        enum<Plan.UscPlan>().bind()
    } else {
        enum<Plan.OnefitPlan>().bind()
    }
}

fun Arb.Companion.url() = arbitrary {
    Url(
        "https://${
            string(
                minSize = 5,
                maxSize = 15,
                codepoints = Codepoint.alphanumeric()
            ).bind()
        }.${string(minSize = 3, maxSize = 3, codepoints = Codepoint.az()).bind()}"
    )
}

fun Arb.Companion.imageUrl() = arbitrary {
    val fileName =
        string(minSize = 3, maxSize = 15, codepoints = Codepoint.az()).bind() + ".png"
    Url("${url().bind()}/${fileName}")
}

fun Arb.Companion.slug() = arbitrary {
    string(minSize = 3, maxSize = 8, codepoints = Codepoint.alphanumeric()).bind()
}

package com.github.seepick.uscclient.shared

import kotlinx.serialization.json.Json

internal val jsonSerializer = Json {
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    prettyPrint = true
    useArrayPolymorphism = false
    ignoreUnknownKeys = true
}

internal fun Json.toPrettyString(jsonString: String) =
    encodeToString(parseToJsonElement(jsonString))

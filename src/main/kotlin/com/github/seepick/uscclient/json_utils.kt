package com.github.seepick.uscclient

import kotlinx.serialization.json.Json

val serializerLenient = Json {
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    prettyPrint = true
    useArrayPolymorphism = false
    ignoreUnknownKeys = true
}

fun Json.toPrettyString(jsonString: String) =
    encodeToString(parseToJsonElement(jsonString))

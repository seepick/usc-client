package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivityInfo
import com.github.seepick.uscclient.checkin.ActivityCheckinEntry
import com.github.seepick.uscclient.checkin.ActivityCheckinEntryType
import com.github.seepick.uscclient.checkin.FreetrainingCheckinEntry
import com.github.seepick.uscclient.venue.VenueDetails
import com.github.seepick.uscclient.venue.VenueInfo
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string

fun Arb.Companion.venueInfo() = arbitrary {
    VenueInfo(
        title = string(minSize = 3, maxSize = 30, codepoints = Codepoint.alphanumeric()).bind(),
        slug = string(minSize = 3, maxSize = 6, codepoints = Codepoint.alphanumeric()).bind(),
        imageUrl = imageUrl().orNull().bind(),
        disciplines = list(string(minSize = 3, maxSize = 8, codepoints = Codepoint.az()), 0..3).bind(),
        addressId = int(min = 1, max = 999).bind(),
        addressDistrict = string(minSize = 3, maxSize = 5, codepoints = Codepoint.alphanumeric()).bind(),
        addressStreet = string(minSize = 5, maxSize = 10, codepoints = Codepoint.alphanumeric()).bind(),
        plan = enum<Plan.UscPlan>().bind(),
    )
}

fun Arb.Companion.venueDetails() = arbitrary {
    VenueDetails(
        title = string(minSize = 5, maxSize = 15, codepoints = Codepoint.az()).bind(),
        slug = string(minSize = 3, maxSize = 5, codepoints = Codepoint.az()).bind(),
        linkedVenueSlugs = list(slug(), 0..3).bind(),
        websiteUrl = url().orNull().bind(),
        description = string(minSize = 3, maxSize = 50).bind(),
        importantInfo = string(minSize = 3, maxSize = 50).orNull().bind(),
        openingTimes = string(minSize = 3, maxSize = 50).orNull().bind(),
        disciplines = list(string(minSize = 3, maxSize = 6), 0..3).bind(),
        longitude = string(minSize = 3, maxSize = 50).bind(),
        latitude = string(minSize = 3, maxSize = 50).bind(),
        postalCode = string(minSize = 3, maxSize = 50).bind(),
        addressLocality = string(minSize = 3, maxSize = 50).bind(),
        streetAddress = string(minSize = 3, maxSize = 50).bind(),
        originalImageUrl = url().orNull().bind(),
    )
}

fun Arb.Companion.activityInfo() = arbitrary {
    ActivityInfo(
        id = int(min = 1).bind(),
        venueSlug = slug().bind(),
        name = string(minSize = 5, maxSize = 20).bind(),
        category = string(minSize = 1, maxSize = 5, codepoints = Codepoint.az()).bind(),
        spotsLeft = int(min = 0, max = 10).bind(),
        dateTimeRange = dateTimeRange().bind(),
        plan = enum<Plan.UscPlan>().bind(),
    )
}

fun Arb.Companion.activityCheckinEntry() = arbitrary {
    ActivityCheckinEntry(
        activityId = int(min = 1).bind(),
        venueSlug = slug().bind(),
        date = localDate().bind(),
        timeRange = timeRange().bind(),
        type = enum<ActivityCheckinEntryType>().bind(),
    )
}

fun Arb.Companion.freetrainingCheckinEntry() = arbitrary {
    FreetrainingCheckinEntry(
        freetrainingId = int(min = 1).bind(),
        venueSlug = slug().bind(),
        date = localDate().bind(),
    )
}

fun Arb.Companion.phpSessionId() = arbitrary {
    PhpSessionId(value = string().bind())
}

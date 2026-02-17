package com.github.seepick.uscclient.shared

import com.github.seepick.uscclient.utils.workParallel
import kotlin.math.max

internal interface Pageable {
    val showMore: Boolean
}

internal suspend fun <P : Pageable> fetchPageable(
    pageSizeHint: Int,
    fetcher: suspend (Int) -> P,
): List<P> {
    val result = mutableListOf<P>()
    do {
        val pages = workParallel(max(1, pageSizeHint / 2), (1..pageSizeHint).toList()) { pageNumber ->
            fetcher(pageNumber)
        }
        result += pages
    } while (pages.all { it.showMore })
    return result
}

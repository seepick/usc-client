package com.github.seepick.uscclient

import kotlin.math.max

interface Pageable {
    val showMore: Boolean
}

suspend fun <P : Pageable> fetchPageable(
    pageSizeHint: Int,
    fetcher: suspend (Int) -> P,
): List<P> {
    val result = mutableListOf<P>()
    do {
        // FIXME bug here; not going beyond 20 pages!
        val pages = workParallel(max(1, pageSizeHint / 2), (1..pageSizeHint).toList()) { pageNumber ->
            fetcher(pageNumber)
        }
        result += pages
    } while (pages.all { it.showMore })
    return result
}

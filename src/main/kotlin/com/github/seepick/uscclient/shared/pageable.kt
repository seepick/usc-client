package com.github.seepick.uscclient.shared

import com.github.seepick.uscclient.utils.workParallel
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

internal interface Pageable {
    val showMore: Boolean
}

fun interface PageProgressListener {
    fun onPageProgress(pageNr: Int)
}

internal suspend fun <P : Pageable> fetchPageable(
    pageSizeHint: Int,
    listener: PageProgressListener = {},
    fetcher: suspend (Int) -> P,
): List<P> {
    val result = mutableListOf<P>()
    val pageCounter = AtomicInteger(0)
    listener.onPageProgress(0)
    do {
        val pages = workParallel(
            coroutineCount = max(1, pageSizeHint / 2),
            data = (1..pageSizeHint).toList()
        ) { pageNumber ->
            fetcher(pageNumber).also {
                listener.onPageProgress(pageCounter.incrementAndGet())
            }
        }
        result += pages
    } while (pages.all { it.showMore }) // end reached when a single page has !showMore
    return result
}

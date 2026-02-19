package com.github.seepick.uscclient.shared

import com.github.seepick.uscclient.utils.workParallel
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

internal interface Pageable {
    val showMore: Boolean

    companion object {} // for extensions
}

fun interface PageProgressListener {
    fun onPageProgress(pageNr: Int)
}

internal suspend fun <P : Pageable> fetchPageable(
    pageSizeHint: Int,
    listener: PageProgressListener = {},
    fetcher: suspend (Int) -> P,
): List<P> {
    require(pageSizeHint > 0) { "pageSizeHint($pageSizeHint) must be > 0" }
    val result = mutableListOf<P>()
    val pageCounter = AtomicInteger(0)
    listener.onPageProgress(0)
    var loopNr = 0
    do {
        val start = 1 + (loopNr * pageSizeHint)
        val pageRange = start..<(start + pageSizeHint)
        val pages = workParallel(
            coroutineCount = max(1, pageSizeHint / 2),
            data = pageRange.toList(),
        ) { pageNumber ->
            if (pageCounter.get() > 100_000) {
                error("Possible infinite loop detected; aborting!")
            }
            fetcher(pageNumber).also {
                listener.onPageProgress(pageCounter.incrementAndGet())
            }
        }
        loopNr++
        result += pages
    } while (pages.all { it.showMore }) // end reached when a single page has not more to show
    return result
}

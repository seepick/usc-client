package com.github.seepick.uscclient.shared

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class FetchPageableTest : StringSpec({
    "Given hint > exist When fetch Then fetch in single go" {
        val pagesExist = 3
        val pageSizeHint = 10

        val pages = fetchPageable(
            pageSizeHint = pageSizeHint,
            fetcher = { pageNr -> PageableStub(pageNr, pageNr <= pagesExist) },
        )

        pages.map { it.pageNr } shouldContainExactlyInAnyOrder List(pageSizeHint) { it + 1 }
        pages.filter { it.showMore }.map { it.pageNr } shouldContainExactlyInAnyOrder List(pagesExist) { it + 1 }
    }
    "Given exist > hint When fetch Then fetch multiple times" {
        val pagesExist = 41
        val pageSizeHint = 20
        val expectedReturned = 3 * pageSizeHint

        val pages = fetchPageable(
            pageSizeHint = pageSizeHint,
            fetcher = { pageNr -> PageableStub(pageNr, pageNr <= pagesExist) },
        )

        pages.map { it.pageNr } shouldContainExactlyInAnyOrder List(expectedReturned) { it + 1 }
        pages.filter { it.showMore }.map { it.pageNr } shouldContainExactlyInAnyOrder List(pagesExist) { it + 1 }
    }
})

internal data class PageableStub(
    val pageNr: Int,
    override val showMore: Boolean,
) : Pageable

internal operator fun Pageable.Companion.invoke(showMore: Boolean) = object : Pageable {
    override val showMore = showMore
}

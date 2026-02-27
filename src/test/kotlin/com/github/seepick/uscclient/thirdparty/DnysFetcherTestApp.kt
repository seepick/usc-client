package com.github.seepick.uscclient.thirdparty

import com.github.seepick.uscclient.shared.DateRange
import com.github.seepick.uscclient.shared.NoopResponseStorage
import com.github.seepick.uscclient.shared.buildHttpClient
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.time.LocalDate

object DnysFetcherTestApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val httpClient = buildHttpClient(URL("https://ignored.com"))
        runBlocking {
            val today = LocalDate.now()
            val events = DnysFetcher(httpClient, NoopResponseStorage).fetchEvents(
                DateRange(from = today, to = today)
            )
            println("received ${events.size} events:")
            events.forEach {
                println("- $it")
            }
        }
    }
}

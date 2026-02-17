package com.github.seepick.uscclient.checkin

import com.github.seepick.uscclient.buildApiFacade
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

object CheckinManualTestApp {
    private val api = buildApiFacade()
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val response = api.fetchCheckinsPage(1, LocalDate.now())
            println("received ${response.entries.size} checkins:")
            response.entries.forEach { entry ->
                println(entry)
            }
        }
    }
}

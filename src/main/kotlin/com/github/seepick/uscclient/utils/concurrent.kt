package com.github.seepick.uscclient.utils

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.min

private val log = logger {}

// TODO rework as service with interface, so can be mocked out easily during testing (?)
// duplicate from LSC
suspend fun <T, R> workParallel(
    coroutineCount: Int,
    data: List<T>,
    processor: suspend (T) -> R,
): List<R> {
    return withContext(Dispatchers.IO) {
        val result = Collections.synchronizedList(mutableListOf<R>())
        val items = ConcurrentLinkedQueue(data.toMutableList())
        (1..min(coroutineCount, data.size)).map { coroutine ->
            log.debug { "Starting coroutine $coroutine/$coroutineCount ..." }
            launch {
                var item = items.poll()
                while (item != null) {
                    result += processor(item)
                    item = items.poll()
                }
            }
        }.joinAll()
        result
    }
}

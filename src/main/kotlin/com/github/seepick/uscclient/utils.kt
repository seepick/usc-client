package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.min
import kotlin.reflect.full.isSuperclassOf

private val log = logger {}

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

suspend fun <T> retrySuspended(
    maxAttempts: Int,
    suppressExceptions: List<Class<out Exception>>,
    code: suspend () -> T,
): T =
    doRetrySuspend(maxAttempts = maxAttempts, suppressExceptions, currentAttempt = 1, code)

private suspend fun <T> doRetrySuspend(
    maxAttempts: Int,
    suppressExceptions: List<Class<out Exception>>,
    currentAttempt: Int,
    code: suspend () -> T,
): T =
    try {
        code()
    } catch (e: Exception) {
        if (suppressExceptions.any { it.kotlin.isSuperclassOf(e::class) } && currentAttempt < maxAttempts) {
            doRetrySuspend(maxAttempts = maxAttempts, suppressExceptions, currentAttempt = currentAttempt + 1, code)
        } else throw e
    }

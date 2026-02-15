package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.util.date.getTimeMillis
import java.util.concurrent.LinkedBlockingQueue

data class SyncStep(
    val section: String,
    val detail: String? = null,
)

object DummySyncProgress : SyncProgress {
    private val listeners = mutableListOf<SyncProgressListener>()
    override fun register(listener: SyncProgressListener) {
        listeners += listener
    }

    override fun start() {
        listeners.forEach(SyncProgressListener::onSyncStart)
    }

    override fun stop(isError: Boolean) {
        listeners.forEach {
            it.onSyncFinish(isError)
        }
    }

    override fun onProgress(step: String, subStep: String?) {
        listeners.forEach {
            it.onSyncStep(SyncStep(step, subStep))
        }
    }

}

interface SyncProgress {
    fun register(listener: SyncProgressListener)
    fun start()
    fun stop(isError: Boolean)
    fun onProgress(step: String, subStep: String? = null)
}

class SyncProgressThreaded : SyncProgress {
    private val log = logger {}

    private val listeners = mutableListOf<SyncProgressListener>()
    private val steps = LinkedBlockingQueue<SyncStep>()
    private val minimumDelayInMs = 300
    private var currentThread: Thread? = null
    private var isFirst = true
    private var shouldStop = false
    private val finalSyncStep = SyncStep("")

    override fun register(listener: SyncProgressListener) {
        log.debug { "register(listener.class=${listener::class.qualifiedName})" }
        listeners += listener
    }

    override fun start() {
        log.debug { "start()" }
        listeners.forEach(SyncProgressListener::onSyncStart)
        isFirst = true
        shouldStop = false
        currentThread = Thread(null, {
            var lastExecution = getTimeMillis()
            while (!shouldStop) {
                log.debug { "Wait to take..." }
                val step = steps.take()
                if (step == finalSyncStep) {
                    log.debug { "Received final step." }
                    continue
                }
                log.debug { "Took: $step" }
                if (!isFirst) {
                    val timeNeeded = getTimeMillis() - lastExecution
                    val delayAdded = minimumDelayInMs - timeNeeded

                    if (delayAdded > 0) {
                        log.debug { "Adding artificial delay of ${delayAdded}ms" }
                        try {
                            Thread.sleep(delayAdded)
                        } catch (e: InterruptedException) {
                            // do nothing
                        }
                    }
                }
                lastExecution = getTimeMillis()
                listeners.forEach { it.onSyncStep(step) }
                isFirst = false
            }
            log.debug { "Progress thread finished." }
        }, "SyncProgressThread").also { it.start() }
    }

    override fun stop(isError: Boolean) {
        log.debug { "stop(isError=$isError)" }
        shouldStop = true
        steps.offer(finalSyncStep)
        currentThread!!.interrupt()
        listeners.forEach {
            it.onSyncFinish(isError)
        }
    }

    override fun onProgress(step: String, subStep: String?) {
        log.debug { "onProgress(section=$step, detail=$subStep)" }
        steps.offer(SyncStep(step, subStep))
    }
}

interface SyncProgressListener {
    fun onSyncStart()
    fun onSyncStep(syncStep: SyncStep)
    fun onSyncFinish(isError: Boolean)
}

package com.github.seepick.uscclient.sync

data class SyncStep(
    val section: String,
    val detail: String? = null,
)

interface SyncProgressListener {
    fun onSyncStart()
    fun onSyncStep(syncStep: SyncStep)
    fun onSyncFinish(isError: Boolean)
}

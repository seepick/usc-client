package com.github.seepick.uscclient.thirdparty

import kotlinx.serialization.Serializable

@Serializable
internal data class DnysJson(
    val payload: List<DnysPayloadJson>,
    val pagination: DnysPaginationJson,
)

@Serializable
internal data class DnysPayloadJson(
    val sessionName: String, // title
    val startsAt: String, // |2026-02-27T10:30:00.000Z
    val endsAt: String,
    val teacher: String,
    val inPerson: Boolean,
)


@Serializable
internal data class DnysPaginationJson(
    val page: Int,
    val pageSize: Int,
    val totalCount: Int, // total number of items across all pages
)

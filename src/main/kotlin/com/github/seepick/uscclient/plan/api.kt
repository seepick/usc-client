package com.github.seepick.uscclient.plan

import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.safeGet
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.statement.bodyAsText

/** A requirement to be injected via the koin module by the user. */
public interface PlanRepository {
    fun selectPlan(): Plan?
    fun updatePlan(plan: Plan)
}

internal interface MembershipApi {
    suspend fun fetch(session: PhpSessionId): Membership
}

internal class MembershipHttpApi(
    private val http: HttpClient,
    private val responseStorage: ResponseStorage,
) : MembershipApi {

    private val log = logger {}

    override suspend fun fetch(session: PhpSessionId): Membership {
        log.debug { "fetch()" }
        val response = http.safeGet("/profile/membership") {
            cookie("PHPSESSID", session.value)
        }
        responseStorage.store(response, "Membership")
        return MembershipParser.parse(response.bodyAsText())
    }
}

// FIXME move to LSC?!
internal class CachedPlanOrFetchProvider(
    private val planRepo: PlanRepository,
    private val membershipApi: MembershipApi,
) {
    private val log = logger {}

    private var cached: Plan? = null

    // TODO not used internally?! thus in LSC only; refactor (or maybe use in here, to simplify sync infra...?)
    suspend fun provide(sessionId: PhpSessionId): Plan {
        log.debug { "provide(..)" }
        if (cached == null) {
            val storedPlan = planRepo.selectPlan()
            cached = if (storedPlan == null) {
                val fetched = membershipApi.fetch(sessionId).plan
                planRepo.updatePlan(fetched)
                fetched
            } else {
                storedPlan
            }
        }
        return cached!!
    }
}

package com.github.seepick.uscclient

// TODO nooo
//class PlanProvider(
////    private val singlesService: SinglesService,
//    private val membershipApi: MembershipApi,
//) {
//    private val log = logger {}
//
//    private var cached: Plan? = null
//
//    suspend fun provide(sessionId: PhpSessionId): Plan {
//        log.debug { "provide(..)" }
//        if (cached == null) {
//            val storedPlan = singlesService.plan
//            cached = if (storedPlan == null) {
//                val fetched = membershipApi.fetch(sessionId).plan
//                singlesService.plan = fetched
//                fetched
//            } else {
//                storedPlan
//            }
//        }
//        return cached!!
//    }
//}

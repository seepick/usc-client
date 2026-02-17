package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger

private val log = logger {}

//fun uscClientModule(
//    config: ApiConfig,
//    planRepo: PlanRepository,
//) = module {
//    // TODO no mock in src/main production code; make app start within src/test, and provide mock impl as test fixture
//    when (config.apiMode) {
//        ApiMode.Mock -> {
//            log.debug { "Wiring mocked USC API." }
//            singleOf(::UscApiMock) bind UscApi::class
//            single { MockPhpSessionProvider } bind PhpSessionProvider::class
//        }
//
//        ApiMode.RealHttp -> {
//            single { if (config.responseLogFolder != null) ResponseStorageImpl(config.responseLogFolder) else NoopResponseStorage } bind ResponseStorage::class
//            single { LoginHttpApi(get(), config.baseUrl) } bind LoginApi::class
//            singleOf(::VenueHttpApi) bind VenueApi::class
//            single { ActivityHttpApi(get(), get(), get(), get()) } bind ActivityApi::class
//            singleOf(::ScheduleHttpApi) bind ScheduleApi::class
//            singleOf(::CheckinHttpApi) bind CheckinApi::class
//            singleOf(::BookingHttpApi) bind BookingApi::class
//            singleOf(::MembershipHttpApi) bind MembershipApi::class
//            singleOf(::UscApiFacade) bind UscApi::class
//            singleOf(::PhpSessionProviderImpl) bind PhpSessionProvider::class
//
//            single { CachedPlanOrFetchProvider(planRepo, get()) }
//        }
//    }
//}

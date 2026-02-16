package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivityApi
import com.github.seepick.uscclient.activity.ActivityHttpApi
import com.github.seepick.uscclient.booking.BookingApi
import com.github.seepick.uscclient.booking.BookingHttpApi
import com.github.seepick.uscclient.checkin.CheckinApi
import com.github.seepick.uscclient.checkin.CheckinHttpApi
import com.github.seepick.uscclient.login.LoginApi
import com.github.seepick.uscclient.login.LoginHttpApi
import com.github.seepick.uscclient.login.MockPhpSessionProvider
import com.github.seepick.uscclient.login.PhpSessionProvider
import com.github.seepick.uscclient.login.PhpSessionProviderImpl
import com.github.seepick.uscclient.plan.CachedPlanOrFetchProvider
import com.github.seepick.uscclient.plan.MembershipApi
import com.github.seepick.uscclient.plan.MembershipHttpApi
import com.github.seepick.uscclient.plan.PlanRepository
import com.github.seepick.uscclient.schedule.ScheduleApi
import com.github.seepick.uscclient.schedule.ScheduleHttpApi
import com.github.seepick.uscclient.shared.NoopResponseStorage
import com.github.seepick.uscclient.shared.ResponseStorage
import com.github.seepick.uscclient.shared.ResponseStorageImpl
import com.github.seepick.uscclient.venue.VenueApi
import com.github.seepick.uscclient.venue.VenueHttpApi
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val log = logger {}

// TODO write integration test
fun uscClientModule(
    config: ApiConfig,
    planRepo: PlanRepository,
) = module {
    // TODO no mock in src/main production code; make app start within src/test, and provide mock impl as test fixture
    when (config.apiMode) {
        ApiMode.Mock -> {
            log.debug { "Wiring mocked USC API." }
            singleOf(::UscApiMock) bind UscApi::class
            single { MockPhpSessionProvider } bind PhpSessionProvider::class
        }

        ApiMode.RealHttp -> {
            single { if (config.usc.storeResponses) ResponseStorageImpl(config.responseLogFolder) else NoopResponseStorage } bind ResponseStorage::class
            single { LoginHttpApi(get(), config.usc.baseUrl) } bind LoginApi::class
            singleOf(::VenueHttpApi) bind VenueApi::class
            single { ActivityHttpApi(get(), get(), get(), get()) } bind ActivityApi::class
            singleOf(::ScheduleHttpApi) bind ScheduleApi::class
            singleOf(::CheckinHttpApi) bind CheckinApi::class
            singleOf(::BookingHttpApi) bind BookingApi::class
            singleOf(::MembershipHttpApi) bind MembershipApi::class
            singleOf(::UscApiFacade) bind UscApi::class
            singleOf(::PhpSessionProviderImpl) bind PhpSessionProvider::class

            single { CachedPlanOrFetchProvider(planRepo, get()) }
        }
    }
}

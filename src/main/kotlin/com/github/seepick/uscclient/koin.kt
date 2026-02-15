package com.github.seepick.uscclient

import com.github.seepick.uscclient.activity.ActivityApi
import com.github.seepick.uscclient.activity.ActivityHttpApi
import com.github.seepick.uscclient.booking.BookingApi
import com.github.seepick.uscclient.booking.BookingHttpApi
import com.github.seepick.uscclient.checkin.CheckinApi
import com.github.seepick.uscclient.checkin.CheckinHttpApi
import com.github.seepick.uscclient.plan.MembershipApi
import com.github.seepick.uscclient.plan.MembershipHttpApi
import com.github.seepick.uscclient.schedule.ScheduleApi
import com.github.seepick.uscclient.schedule.ScheduleHttpApi
import com.github.seepick.uscclient.venue.VenueApi
import com.github.seepick.uscclient.venue.VenueHttpApi
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

private val log = logger {}

@JvmInline
value class PhpSessionId(val value: String) {
    override fun toString() = value
}

data class ApiConfig(
    val apiMode: ApiMode,
//    val sync: SyncMode,
    val usc: UscConfig = UscConfig(),
//    val logFileEnabled: Boolean = false,
    val responseLogFolder: File, // api logs
)

fun apiModule(
    config: ApiConfig,
) = module {
    // TODO no mock in src/main production code; make app start within src/test, and provide mock impl as test fixture
    when (config.apiMode) {
        ApiMode.Mock -> {
            log.debug { "Wiring mocked USC API." }
            singleOf(::MockUscApi) bind UscApi::class
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
            singleOf(::UscApiAdapter) bind UscApi::class
            singleOf(::PhpSessionProviderImpl) bind PhpSessionProvider::class
//            singleOf(::PlanProvider) NO! no state here; fun provide(sessionId: PhpSessionId): Plan
        }
    }

}

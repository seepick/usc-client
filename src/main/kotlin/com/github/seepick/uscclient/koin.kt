package seepick.localsportsclub.api

import com.github.seepick.uscclient.MockUscApi
import com.github.seepick.uscclient.UscApi
import com.github.seepick.uscclient.UscApiAdapter
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import seepick.localsportsclub.ApiMode
import seepick.localsportsclub.AppConfig
import seepick.localsportsclub.api.activity.ActivityApi
import seepick.localsportsclub.api.activity.ActivityHttpApi
import seepick.localsportsclub.api.booking.BookingApi
import seepick.localsportsclub.api.booking.BookingHttpApi
import seepick.localsportsclub.api.checkin.CheckinApi
import seepick.localsportsclub.api.checkin.CheckinHttpApi
import seepick.localsportsclub.api.plan.MembershipApi
import seepick.localsportsclub.api.plan.MembershipHttpApi
import seepick.localsportsclub.api.schedule.ScheduleApi
import seepick.localsportsclub.api.schedule.ScheduleHttpApi
import seepick.localsportsclub.api.venue.VenueApi
import seepick.localsportsclub.api.venue.VenueHttpApi

private val log = logger {}

@JvmInline
value class PhpSessionId(val value: String) {
    override fun toString() = value
}

fun apiModule(config: AppConfig) = module {
    if (config.api == ApiMode.Mock) {
        log.debug { "Wiring mocked USC API." }
        singleOf(::MockUscApi) bind UscApi::class
        single { MockPhpSessionProvider } bind PhpSessionProvider::class

    } else if (config.api == ApiMode.RealHttp) {
        single { if (config.usc.storeResponses) ResponseStorageImpl() else NoopResponseStorage } bind ResponseStorage::class
        single { LoginHttpApi(get(), config.usc.baseUrl) } bind LoginApi::class
        singleOf(::VenueHttpApi) bind VenueApi::class
        single { ActivityHttpApi(get(), get(), get(), get()) } bind ActivityApi::class
        singleOf(::ScheduleHttpApi) bind ScheduleApi::class
        singleOf(::CheckinHttpApi) bind CheckinApi::class
        singleOf(::BookingHttpApi) bind BookingApi::class
        singleOf(::MembershipHttpApi) bind MembershipApi::class
        singleOf(::UscApiAdapter) bind UscApi::class
        singleOf(::PhpSessionProviderImpl) bind PhpSessionProvider::class
        singleOf(::PlanProvider)
    }
}

package com.github.seepick.uscclient

/*
TODO rethink design


suspended fun lscMain() {
  val config = UscConfig(
    credentials = Credentials("foo", "bar"),
    language = Language.English,
    responseLogFolder = File("usc-responses") as File?,
//    NO, fetched at runtime: city = City.Berlin,
//    NO, fetched at runtime: plan = Plan.UscPlan.Free,
  )
  val connector = UscConnectorImpl()
  val uscClient = connector.connect(config)
  currentUscClient.value = uscClient
  ..
  uscClient.fetchVenues(VenuesFilter) // can be changed in UI, thus pass runtime (not handled internally)
}


interface UscConnector {
    fun connect(config: UscConfig): UscClient
}

interface UscClient {
    suspend fun fetchVenues(filter: VenuesFilter): List<VenueInfo>
}

internal class UscConnectorImpl : UscConnector {
    private val log = logger {}
    override fun connect(config: UscConfig): UscClient {
        log.debug { "connect(config)" }
        val phpSessionId = login(config.credentials)
        // fetch plan/membership, city, etc. here, to avoid doing it later in the API calls (and thus multiple times)
        return UscClientImpl(phpSessionId)
    }
}

internal class UscClientImpl(phpSessionId, delegate) : UscClient {
    private val log = logger {}
    override fun fetchFoo(): String {
        log.debug { "fetchFoo()" }
        return delegate.fetchFoo(phpSessionId)
    }
}
*/

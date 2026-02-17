# Todo

* first release & integrate; then refactor/redesign

* delayed, cached impl (to read creds at runtime, from UI for example)
* get rid of koin; manually wire object tree
    * get rid of globals (serializer, http client); inject properly
        * provide `operator fun UscApi.invoke` to return impl and wire object tree
* !!! read creds from .ignored file (easier startup everywhere)
* enhance system test app; print all venues; for easier debugging
* internally manage php session ID
* move tests in proper package
* resolve all todos
* system tests (enable-able), searching for creds in filesystem + sys/env-vars; otherwise fail
    * only run locally (add bin/validate.sh file)
* !!! BUGFIX: scheduled sync broken

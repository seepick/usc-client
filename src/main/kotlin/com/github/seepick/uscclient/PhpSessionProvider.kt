package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking

interface PhpSessionProvider {
    fun provide(): PhpSessionId
}

object MockPhpSessionProvider : PhpSessionProvider {
    override fun provide(): PhpSessionId = PhpSessionId("mock")
}

class PhpSessionProviderImpl(
//    private val singlesService: SinglesService,
    private val loginApi: LoginApi,
    private val credentials: Credentials,
) : PhpSessionProvider {
    private val log = logger {}

    private val cached: PhpSessionId by lazy {
        runBlocking {
            log.info { "Creating cached PHP Session ID..." }
            when (val result = loginApi.login(credentials)) {
                is LoginResult.Failure -> throw UscLoginException(result.message)
                is LoginResult.Success -> result.phpSessionId
            }
        }
    }

    override fun provide(): PhpSessionId = cached
}

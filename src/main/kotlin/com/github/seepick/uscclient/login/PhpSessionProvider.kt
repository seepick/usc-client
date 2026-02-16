package com.github.seepick.uscclient.login

import com.github.seepick.uscclient.UscException
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking

internal interface PhpSessionProvider {
    fun provide(): PhpSessionId
}

internal object MockPhpSessionProvider : PhpSessionProvider {
    override fun provide(): PhpSessionId = PhpSessionId("mock")
}

internal class PhpSessionProviderImpl(
    private val loginApi: LoginApi,
    private val credentials: Credentials, // TODO make it delayed `credentials: () -> Credentials`
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

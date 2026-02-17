package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.login.LoginHttpApi
import com.github.seepick.uscclient.login.LoginResult
import com.github.seepick.uscclient.login.UscLoginException
import com.github.seepick.uscclient.model.UscLang
import com.github.seepick.uscclient.shared.buildHttpClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger


internal class UscConnectorImpl : UscConnector {
    private val log = logger {}

    override suspend fun connect(config: UscConfig): UscApi {
        log.info { "Connecting to ${config}..." }
        val httpClient = buildHttpClient(baseUrl = config.baseUrl)
        when (val loginResult = LoginHttpApi(httpClient).login(config.credentials)) {
            is LoginResult.Failure -> throw UscLoginException(loginResult.message)
            is LoginResult.Success -> return UscApiFacade(
                phpSessionId = loginResult.phpSessionId,
                httpClient = httpClient,
                responseLogFolder = config.responseLogFolder,
                currentYear = config.currentYear,
            )
        }
    }

    override suspend fun verifyConnection(credentials: Credentials, lang: UscLang): ConnectionVerificationResult {
        log.info { "Verifying connection for ${credentials}/$lang..." }
        val httpClient = buildHttpClient(baseUrl = lang.baseUrl)
        return when (val loginResult = LoginHttpApi(httpClient).login(credentials)) {
            is LoginResult.Success -> ConnectionVerificationResult.Success
            is LoginResult.Failure -> ConnectionVerificationResult.Failure(loginResult.message)
        }
    }
}

public sealed class ConnectionVerificationResult {
    public object Success : ConnectionVerificationResult()
    public data class Failure(val message: String) : ConnectionVerificationResult()
}

public class UscConnectorMock : UscConnector {
    override suspend fun connect(config: UscConfig) = UscApiMock()
    override suspend fun verifyConnection(credentials: Credentials, lang: UscLang) =
        ConnectionVerificationResult.Success
}

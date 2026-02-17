package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.LoginHttpApi
import com.github.seepick.uscclient.login.LoginResult
import com.github.seepick.uscclient.login.UscLoginException
import com.github.seepick.uscclient.shared.buildHttpClient
import java.net.URL

internal class UscConnectorImpl : UscConnector {
    override suspend fun connect(config: UscConfig): UscApi {
        val httpClient = buildHttpClient(baseUrl = URL("https://urbansportsclub.com/${config.lang.urlCode}"))
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
}

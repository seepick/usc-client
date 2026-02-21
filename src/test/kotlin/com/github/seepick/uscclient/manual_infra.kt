package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.login.LoginHttpApi
import com.github.seepick.uscclient.login.LoginResult
import com.github.seepick.uscclient.login.PhpSessionId
import com.github.seepick.uscclient.login.loadLocal
import com.github.seepick.uscclient.login.localCredsFile
import com.github.seepick.uscclient.model.UscLang
import com.github.seepick.uscclient.shared.buildHttpClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.LocalDate
import java.util.Properties

private val log = logger {}

internal fun buildApiFacade(
    responseLogFolder: File? = File("build/test_utils-api_logs"),
): UscApiFacade {
    val httpClient = buildHttpClient(baseUrl = UscLang.English.baseUrl)
    val phpSessionId = loadLocalPhpSessionId() ?: loginAndGetPhpSessionId(httpClient)
    return UscApiFacade(
        phpSessionId = phpSessionId,
        httpClient = httpClient,
        responseLogFolder = responseLogFolder,
        currentYear = LocalDate.now().year,
    )
}

private fun loadLocalPhpSessionId(): PhpSessionId? {
    val syspropSessionId = System.getProperty("phpSessionId")
    if (syspropSessionId != null) {
        log.info { "Using system property's session ID: $syspropSessionId" }
        return PhpSessionId(syspropSessionId)
    }

    log.debug { "Checking: ${localCredsFile.canonicalPath}" }
    if (localCredsFile.exists()) {
        val props = Properties().also {
            it.load(localCredsFile.reader().buffered())
        }
        return props["phpSessionId"]?.toString()?.trim()?.ifEmpty { null }?.let {
            log.info { "Using PHP session ID from local file ${localCredsFile.absolutePath}" }
            PhpSessionId(it)
        }
    }
    return null
}

private fun loginAndGetPhpSessionId(httpClient: HttpClient) = runBlocking {
    LoginHttpApi(httpClient).login(Credentials.loadLocal())
        .shouldBeInstanceOf<LoginResult.Success>().phpSessionId
}

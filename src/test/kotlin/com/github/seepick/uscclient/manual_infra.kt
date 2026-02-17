package com.github.seepick.uscclient

import com.github.seepick.uscclient.login.Credentials
import com.github.seepick.uscclient.login.LoginHttpApi
import com.github.seepick.uscclient.login.LoginResult
import com.github.seepick.uscclient.login.PhpSessionId
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
private val localFile = File(".local-usc-credentials.properties")

internal fun buildApiFacade(
    responseLogFolder: File? = File("build/test_utils-api_logs"),
): UscApiFacade {
    val httpClient = buildHttpClient(baseUrl = UscLang.English.baseUrl)
    val phpSessionId = loadPhpSessionId() ?: phpSessionIdByLogin(httpClient)
    return UscApiFacade(
        phpSessionId = phpSessionId,
        httpClient = httpClient,
        responseLogFolder = responseLogFolder,
        currentYear = LocalDate.now().year,
    )
}

private fun loadPhpSessionId(): PhpSessionId? {
    val syspropSessionId = System.getProperty("phpSessionId")
    if (syspropSessionId != null) {
        log.info { "Using system property's session ID: $syspropSessionId" }
        return PhpSessionId(syspropSessionId)
    }
    log.debug { "Checking: ${localFile.canonicalPath}" }
    if (localFile.exists()) {
        val props = Properties().also {
            it.load(localFile.reader().buffered())
        }
        return props["phpSessionId"]?.toString()?.trim()?.ifEmpty { null }?.let {
            log.info { "Using PHP session ID from local file ${localFile.absolutePath}" }
            PhpSessionId(it)
        }
    }
    return null
}

private fun phpSessionIdByLogin(httpClient: HttpClient) = runBlocking {
    LoginHttpApi(httpClient).login(loadCredentialsOrThrow())
        .shouldBeInstanceOf<LoginResult.Success>().phpSessionId
}

private fun loadCredentialsOrThrow(): Credentials {
    val syspropUsername = System.getProperty("username")
    val syspropPassword = System.getProperty("password")
    if (syspropUsername != null && syspropPassword != null) {
        log.info { "Using credentials from system property." }
        return Credentials(syspropUsername, syspropPassword)
    }
    if (localFile.exists()) {
        log.info { "Using credentials from local file ${localFile.absolutePath}" }
        val props = Properties().also {
            it.load(localFile.reader().buffered())
        }
        return Credentials(props.getProperty("username"), props.getProperty("password"))
    }
    error("No credentials provided via sys-props or in: ${localFile.canonicalPath}.")
    // TODO move back to LSC
//            cliConnectToDatabase(isProd = false)
//            println("Using credentials from exposed repository.")
//            SinglesServiceImpl(ExposedSinglesRepo).preferences.uscCredentials ?: error("No credentials stored in DB")
}

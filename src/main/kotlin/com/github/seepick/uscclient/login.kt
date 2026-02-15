package com.github.seepick.uscclient

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.jsoup.Jsoup

class UscLoginException(message: String) : Exception(message)

interface LoginApi {
    suspend fun login(credentials: Credentials): LoginResult
}

class LoginHttpApi(
    private val http: HttpClient,
    private val baseUrl: Url,
) : LoginApi {

    private val log = logger {}

    override suspend fun login(credentials: Credentials): LoginResult {
        log.info { "logging in as: ${credentials.username}" }
        val home = loadHome()
        return submitLogin(
            LoginRequest(
                email = credentials.username,
                password = credentials.password,
                phpSessionId = home.phpSessionId,
                secret = home.loginSecret,
            )
        ).also {
            when (it) {
                is LoginResult.Failure -> log.info { "Failed to log in." }
                is LoginResult.Success -> log.info { "Successfully logged in ✅👍🏻" }
            }
        }
    }

    private data class HomeResponse(
        val loginSecret: Pair<String, String>,
        val phpSessionId: PhpSessionId,
    )

    private suspend fun loadHome(): HomeResponse {
        log.debug { "Requesting home to extract basic session info." }
        val response = http.get(baseUrl)
        response.requireStatusOk()
        val html = HomePageParser.parse(response.bodyAsText())
        return HomeResponse(
            loginSecret = html.loginSecret,
            phpSessionId = response.phpSessionId,
        ).also {
            log.debug { "Extracted: $it" }
        }
    }

    private data class LoginRequest(
        val email: String,
        val password: String,
        val phpSessionId: PhpSessionId,
        val secret: Pair<String, String>,
    )

    private suspend fun submitLogin(login: LoginRequest): LoginResult {
        val response = http.submitForm(
            url = "$baseUrl/login",
            formParameters = parameters {
                append("email", login.email)
                append("password", login.password)
                append(login.secret.first, login.secret.second)
            }
        ) {
            cookie("PHPSESSID", login.phpSessionId.value)
            header("x-requested-with", "XMLHttpRequest") // IMPORTANT! to change the response to JSON!!!
        }
        response.requireStatusOk()
        val jsonSuccessOrHtmlFail = response.bodyAsText()
        try {
            val jsonRoot = Json.parseToJsonElement(jsonSuccessOrHtmlFail)
            return if (jsonRoot.jsonObject["success"].toString() == "true") {
                LoginResult.Success(phpSessionId = response.phpSessionId)
            } else {
                log.warn { "Success != true; returned JSON after login:\n$jsonSuccessOrHtmlFail" }
                LoginResult.Failure("Invalid server response")
            }
        } catch (e: SerializationException) {
            return LoginResult.Failure("Username/password is wrong")
        }
    }
}

sealed interface LoginResult {
    data class Success(val phpSessionId: PhpSessionId) : LoginResult
    data class Failure(val message: String) : LoginResult
}

object HomePageParser {

    data class HomeHtmlResponse(
        val loginSecret: Pair<String, String>, // hidden input in the login form, which needs to be passed through
    )

    fun parse(html: String): HomeHtmlResponse {
        val body = Jsoup.parse(html).body()
        val login = body.getElementById("login-form") ?: error("login-form not found in HTML response:\n\n$html")
        val secret = login.getElementsByTag("input").single {
            it.attr("type") == "hidden" && it.id() != "check"
        }
        return HomeHtmlResponse(
            loginSecret = secret.attr("name") to secret.attr("value"),
        )
    }
}

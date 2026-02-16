package com.github.seepick.uscclient.login

public data class Credentials(
    val username: String,
    val password: String,
) {
    override fun toString() = "Credentials[username=$username, password=***]"
}

public sealed interface LoginResult {
    public data class Success(val phpSessionId: PhpSessionId) : LoginResult
    public data class Failure(val message: String) : LoginResult
}

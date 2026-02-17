package com.github.seepick.uscclient.login

public data class Credentials(
    val username: String,
    val password: String,
) {
    override fun toString() = "Credentials[username=$username, password=***]"
}

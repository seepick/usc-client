package com.github.seepick.uscclient

data class Credentials(
    val username: String,
    val password: String,
) {
    override fun toString() = "Credentials[username=$username, password=***]"
}

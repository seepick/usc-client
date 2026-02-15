package com.github.seepick.uscclient

class MockUscClient : UscClient {
    override fun getFoo(): String {
        return "mock foo response"
    }
}

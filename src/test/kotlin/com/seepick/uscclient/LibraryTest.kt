package com.seepick.uscclient

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class LibraryTest : StringSpec({
    "someLibraryMethod should return true" {
        val classUnderTest = Library()
        classUnderTest.someLibraryMethod() shouldBe true
    }
})

package com.github.seepick.uscclient

import java.io.InputStream

private object ResourceLocator

fun readFromClasspath(classpath: String): String =
    openFromClasspath(classpath).bufferedReader().use {
        it.readText()
    }

fun openFromClasspath(classpath: String): InputStream =
    ResourceLocator::class.java.getResourceAsStream(classpath)
        ?: throw Exception("Classpath resource not found at: [$classpath]")

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("io.kotest") version "0.4.11"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin logging
    implementation(libs.kotlin.logging)
    
    // Kotest
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.property)
    
    // Ktor client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    
    // SLF4J for logging backend
    implementation(libs.slf4j.api)
    runtimeOnly(libs.slf4j.simple)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

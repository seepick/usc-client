plugins {
    kotlin("jvm") version "2.2.21"
    id("io.kotest") version "0.4.11"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    
    // Ktor client
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-cio:3.0.3")
    
    // SLF4J for logging backend
    implementation("org.slf4j:slf4j-api:2.0.16")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

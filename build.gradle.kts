import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "2.3.10"
    id("io.kotest") version "6.1.3"
    `java-library`
    id("maven-publish")
    id("java-test-fixtures")
    id("com.github.ben-manes.versions") version "0.53.0"
}

repositories {
    mavenCentral()
}

dependencies {
    val versionKtor = "3.4.0"
    listOf(
        "client-core",
        "client-apache", // other JVM engines had some (socket) issues: java, cio (maybe jetty, okhttp too)
        "client-logging",
        "client-content-negotiation",
        "serialization-kotlinx-json",
    ).forEach {
        implementation("io.ktor:ktor-$it:$versionKtor")
    }
    implementation("org.jsoup:jsoup:1.21.2")

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.14")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.31")

    val versionKotest = "6.1.3"
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")
    testImplementation("io.kotest:kotest-property:$versionKotest")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.github.seepick"
            artifactId = project.name
            version = project.findProperty("version")?.toString() ?: error("version not specified!")
            pom {
                name.set(project.name)
                description.set("You gonna like this")
                url.set("https://github.com/seepick/usc-client")
            }
        }
    }
}

tasks.withType<DependencyUpdatesTask> {
    val rejectPatterns = listOf(
        ".*-ea.*", ".*RC.*", ".*rc.*", ".*M1", ".*check",
        ".*dev.*", ".*[Bb]eta.*", ".*[Aa]lpha.*", ".*SNAPSHOT.*",
    ).map { Regex(it) }
    rejectVersionIf {
        rejectPatterns.any {
            it.matches(candidate.version)
        }
    }
}

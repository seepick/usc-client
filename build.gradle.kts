import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    `java-library`
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("maven-publish")
    id("java-test-fixtures")
    id("com.github.ben-manes.versions") version "0.53.0"
}

group = "com.github.seepick"
version = "2000.0.SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
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
    implementation("org.jsoup:jsoup:1.22.1")
    val versionKoin = "4.1.1"
    implementation("io.insert-koin:koin-core:$versionKoin")

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.14")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.31")

    val versionKotest = "6.1.3"
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")
    testImplementation("io.kotest:kotest-property:$versionKotest")
    testImplementation("io.insert-koin:koin-test:$versionKoin")
    testImplementation("io.ktor:ktor-client-mock:$versionKtor")
    testImplementation("io.mockk:mockk:1.14.7")
    testFixturesImplementation("io.kotest:kotest-property:$versionKotest")
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
            groupId = project.group.toString()
            artifactId = project.name
            version = project.findProperty("version")?.toString() ?: error("version not specified!")
            suppressAllPomMetadataWarnings() // testFixturesApiElements
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
        ".*dev.*", ".*[Bb]eta.*", ".*[Aa]lpha.*", ".*SNAPSHOT.*"
    ).map { Regex(it) }
    rejectVersionIf {
        rejectPatterns.any {
            it.matches(candidate.version)
        }
    }
}

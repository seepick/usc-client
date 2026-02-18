[![Continuous](https://github.com/seepick/usc-client/actions/workflows/continuous.yml/badge.svg)](https://github.com/seepick/usc-client/actions/workflows/continuous.yml)
[![](https://jitpack.io/v/seepick/usc-client.svg)](https://jitpack.io/#seepick/usc-client)

# usc-client

A naughty Kotlin SDK at your service.

## Setup

Add the JitPack hippo to your silly `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Acknowledge your need in `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.seepick:usc-client:2000.1.1")
}
```

Beeing bleeding edge by using the [latest version](https://jitpack.io/#seepick/usc-client).

Versions scheme is `YYYY.M.X` (year, month, sequence), e.g.:
`2026.1.1` (2026, January, 1st release, thus 1-base indexed, you may forget).

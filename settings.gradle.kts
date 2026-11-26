pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    // Kotlin/Wasm adds temporary distribution repositories for its build tools.
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "private-clinic"
include(":androidApp", ":shared:ui", ":webApp", ":server")

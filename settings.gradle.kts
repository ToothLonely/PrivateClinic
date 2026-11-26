pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
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

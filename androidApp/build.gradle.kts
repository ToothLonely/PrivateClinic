plugins {
    id("clinic.android-application")
    id("clinic.compose")
}

val apiBaseUrl = providers.gradleProperty("clinicApiBaseUrl").orElse("")
    .get().trim().replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "ru.privateclinic.android"
    defaultConfig {
        applicationId = "ru.privateclinic.app"
        versionCode = 1
        versionName = "0.1.0"
        buildConfigField("String", "CLINIC_API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":shared:ui"))
    implementation(libs.android.activity.compose)
    implementation(libs.compose.runtime)
}

plugins {
    id("clinic.android-application")
    id("clinic.compose")
}

android {
    namespace = "ru.privateclinic.android"
    defaultConfig {
        applicationId = "ru.privateclinic.app"
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":shared:ui"))
    implementation(libs.android.activity.compose)
    implementation(libs.compose.runtime)
}

plugins {
    id("clinic.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

val versions = the<VersionCatalogsExtension>().named("libs")
kotlin {
    android {
        compileSdk = versions.findVersion("android-compile-sdk").get().requiredVersion.toInt()
        minSdk = versions.findVersion("android-min-sdk").get().requiredVersion.toInt()
    }
}

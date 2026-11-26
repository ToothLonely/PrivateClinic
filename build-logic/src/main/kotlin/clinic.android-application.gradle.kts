plugins {
    id("com.android.application")
}

val versions = the<VersionCatalogsExtension>().named("libs")
android {
    compileSdk = versions.findVersion("android-compile-sdk").get().requiredVersion.toInt()
    defaultConfig {
        minSdk = versions.findVersion("android-min-sdk").get().requiredVersion.toInt()
        targetSdk = versions.findVersion("android-target-sdk").get().requiredVersion.toInt()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(versions.findVersion("jdk").get().requiredVersion))
    }
}

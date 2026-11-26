plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

val versions = the<VersionCatalogsExtension>().named("libs")
kotlin {
    jvmToolchain(versions.findVersion("jdk").get().requiredVersion.toInt())
}

plugins {
    id("org.jetbrains.kotlin.jvm")
}

val versions = the<VersionCatalogsExtension>().named("libs")
kotlin {
    jvmToolchain(versions.findVersion("jdk").get().requiredVersion.toInt())
}

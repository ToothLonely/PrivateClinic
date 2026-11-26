import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("clinic.android-multiplatform-library")
    id("clinic.serialization")
}

kotlin {
    android {
        namespace = "ru.privateclinic.network"
        withHostTest {}
    }
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.serialization.json)
        }
        androidMain.dependencies { implementation(libs.ktor.client.okhttp) }
        iosMain.dependencies { implementation(libs.ktor.client.darwin) }
        wasmJsMain.dependencies { implementation(libs.ktor.client.js) }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
        val androidHostTest by getting {
            dependencies { implementation(libs.kotlin.test.junit) }
        }
    }
}

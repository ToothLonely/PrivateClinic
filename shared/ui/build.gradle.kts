import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("clinic.android-multiplatform-library")
    id("clinic.compose")
}

kotlin {
    android {
        namespace = "ru.privatclinic.ui"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "ClinicUI"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
    }
}

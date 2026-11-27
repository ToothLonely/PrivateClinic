import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("clinic.multiplatform")
    id("clinic.compose")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "clinic.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.resources)
        }
        wasmJsMain.dependencies {
            implementation(project(":shared:ui"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
    }
}

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("clinic.android-multiplatform-library")
    id("clinic.compose")
}

kotlin {
    android {
        namespace = "ru.privateclinic.ui"
        withHostTest {}
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

    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:network"))
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.coroutines.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.navigation3.ui)
        }
        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(project(":shared:storage"))
            }
        }
        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val androidHostTest by getting {
            dependencies { implementation(libs.kotlin.test.junit) }
        }
    }
}

plugins {
    id("clinic.android-multiplatform-library")
    id("clinic.room")
}

kotlin {
    android { namespace = "ru.privateclinic.storage" }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room { schemaDirectory("$projectDir/schemas") }

package ru.privateclinic.ui

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import ru.privateclinic.storage.createClinicDatabase

fun MainViewController(apiBaseUrl: String?): UIViewController {
    val storage = mobileStorageModule { createClinicDatabase() }
    return ComposeUIViewController { ClinicApp(apiBaseUrl, storage) }
}

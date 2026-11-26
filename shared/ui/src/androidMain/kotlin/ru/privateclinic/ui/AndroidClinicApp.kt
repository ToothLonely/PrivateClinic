package ru.privateclinic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ru.privateclinic.storage.createClinicDatabase

@Composable
fun AndroidClinicApp(apiBaseUrl: String?) {
    val application = LocalContext.current.applicationContext
    val storage = remember(application) { mobileStorageModule { createClinicDatabase(application) } }
    ClinicApp(apiBaseUrl, storage)
}

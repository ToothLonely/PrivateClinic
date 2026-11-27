package ru.privateclinic.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import ru.privateclinic.storage.createClinicDatabase

@Composable
fun AndroidClinicApp(apiBaseUrl: String?) {
    val storage = if (LocalInspectionMode.current) {
        null
    } else {
        val application = LocalContext.current.applicationContext
        remember(application) { mobileStorageModule { createClinicDatabase(application) } }
    }
    ClinicRoot(apiBaseUrl, storage)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AndroidClinicAppPreview() {
    AndroidClinicApp(apiBaseUrl = null)
}

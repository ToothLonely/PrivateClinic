package ru.privateclinic.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.privateclinic.ui.app.AndroidClinicApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AndroidClinicApp(BuildConfig.CLINIC_API_BASE_URL.ifBlank { null }) }
    }
}

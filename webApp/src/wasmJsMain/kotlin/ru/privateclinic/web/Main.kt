package ru.privateclinic.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import private_clinic.webapp.generated.resources.Res
import private_clinic.webapp.generated.resources.noto_sans_bold
import private_clinic.webapp.generated.resources.noto_sans_regular
import ru.privateclinic.ui.app.ClinicApp
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val apiBaseUrl = browserApiBaseUrl()
    ComposeViewport {
        val fontFamily = FontFamily(
            Font(Res.font.noto_sans_regular, FontWeight.Normal),
            Font(Res.font.noto_sans_bold, FontWeight.Bold),
        )
        ClinicApp(apiBaseUrl, fontFamily)
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("""() => new URL(document.querySelector('meta[name="clinic-api-base-url"]')?.content.trim() || '/', document.baseURI).href""")
private external fun browserApiBaseUrl(): String

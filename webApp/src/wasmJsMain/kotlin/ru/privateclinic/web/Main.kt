package ru.privateclinic.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ru.privateclinic.ui.ClinicApp
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val apiBaseUrl = browserApiBaseUrl()
    ComposeViewport { ClinicApp(apiBaseUrl) }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("""() => new URL(document.querySelector('meta[name="clinic-api-base-url"]')?.content.trim() || '/', document.baseURI).href""")
private external fun browserApiBaseUrl(): String

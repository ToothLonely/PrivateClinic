package ru.privateclinic.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ru.privateclinic.ui.ClinicApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport { ClinicApp() }
}

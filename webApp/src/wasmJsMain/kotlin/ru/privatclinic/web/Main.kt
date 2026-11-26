package ru.privatclinic.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ru.privatclinic.ui.ClinicApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport { ClinicApp() }
}

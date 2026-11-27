package ru.privateclinic.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ClinicApp(apiBaseUrl: String?, fontFamily: FontFamily = FontFamily.SansSerif) =
    ClinicRoot(apiBaseUrl, platformModule = null, fontFamily = fontFamily)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ClinicAppPreview() {
    ClinicApp(apiBaseUrl = null)
}

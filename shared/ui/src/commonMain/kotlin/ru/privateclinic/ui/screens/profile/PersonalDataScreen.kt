package ru.privateclinic.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.privateclinic.ui.components.layout.BackScreen
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewProfile
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun PersonalDataScreen(profile: PreviewProfile, wide: Boolean, onBack: () -> Unit) {
    BackScreen(ClinicRoute.PersonalData.title, wide, onBack) {
        PersonalData(profile)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PersonalDataScreenPreview() {
    ClinicTheme(ThemeChoice.Light) {
        PersonalDataScreen(ClinicPreviewData.profile, wide = false, onBack = {})
    }
}

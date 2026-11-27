package ru.privateclinic.ui.screens.appointments

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.privateclinic.ui.components.layout.SectionTitle
import ru.privateclinic.ui.components.row.RouteRow
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun MedicalRecords(onOpen: (ClinicRoute) -> Unit) {
    Spacer(Modifier.height(10.dp))
    SectionTitle("Медкарта")
    Spacer(Modifier.height(10.dp))
    listOf(ClinicRoute.Visits, ClinicRoute.Studies, ClinicRoute.LabResults).forEach { RouteRow(it, onOpen) }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MedicalRecordsPreview() {
    ClinicTheme(ThemeChoice.Light) {
        MedicalRecords(onOpen = {})
    }
}

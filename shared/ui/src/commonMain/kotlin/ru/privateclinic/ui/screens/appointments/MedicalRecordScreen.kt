package ru.privateclinic.ui.screens.appointments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.layout.BackScreen
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun MedicalRecordScreen(title: String, wide: Boolean, onBack: () -> Unit) {
    BackScreen(title, wide, onBack) {
        val center = if (wide) Modifier.fillMaxWidth().weight(1f) else Modifier
        Box(center.padding(top = 10.dp, bottom = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                "Функция появится позже.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = if (wide) 20.sp else 16.sp,
                lineHeight = if (wide) 28.sp else 22.4.sp,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 880)
@Composable
private fun MedicalRecordScreenPreview() {
    ClinicTheme(ThemeChoice.Light) {
        MedicalRecordScreen(ClinicRoute.Visits.title, wide = true, onBack = {})
    }
}

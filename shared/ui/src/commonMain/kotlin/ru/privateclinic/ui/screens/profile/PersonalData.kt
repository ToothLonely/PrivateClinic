package ru.privateclinic.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewProfile
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun PersonalData(profile: PreviewProfile) {
    Column(Modifier.padding(top = 20.dp, bottom = 24.dp)) {
        listOf("Фамилия, имя, отчество" to profile.fullName, "Электронная почта" to profile.email).forEach { (label, value) ->
            Text(label, fontSize = 14.sp, lineHeight = 19.6.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, Modifier.padding(top = 5.dp, bottom = 18.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PersonalDataPreview() {
    ClinicTheme(ThemeChoice.Light) {
        PersonalData(ClinicPreviewData.profile)
    }
}

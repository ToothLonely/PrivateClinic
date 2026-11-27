package ru.privateclinic.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.privateclinic.ui.components.layout.SectionTitle
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun UpcomingAppointments() {
    Spacer(Modifier.height(10.dp))
    SectionTitle("Текущие записи")
    Spacer(Modifier.height(10.dp))
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(15.dp)
    Box(Modifier.fillMaxWidth().heightIn(min = 144.dp).background(c.surfaceVariant, shape)
        .border(1.dp, c.outlineVariant, shape), contentAlignment = Alignment.Center) {
        Text("У вас нет записей", color = c.onSurfaceVariant)
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun UpcomingAppointmentsPreview() {
    ClinicTheme(ThemeChoice.Light) {
        UpcomingAppointments()
    }
}

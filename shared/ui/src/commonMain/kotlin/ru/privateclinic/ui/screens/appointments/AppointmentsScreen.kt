package ru.privateclinic.ui.screens.appointments

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.privateclinic.ui.components.header.ClinicHeader
import ru.privateclinic.ui.components.layout.contentInset
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun AppointmentsScreen(
    wide: Boolean,
    onOpen: (ClinicRoute) -> Unit,
    bookingFocus: FocusRequester,
    onBook: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ClinicHeader(wide)
        BoxWithConstraints(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
            val inset = contentInset(wide, maxWidth)
            val side = (maxWidth * .03f).coerceIn(18.dp, 42.dp)
            Column(Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(inset)) {
                if (wide) {
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Column(Modifier.weight(1f).padding(end = side)) { UpcomingAppointments() }
                        Row(Modifier.weight(1f).height(IntrinsicSize.Min)) {
                            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Column(Modifier.weight(1f).padding(start = side)) { MedicalRecords(onOpen) }
                        }
                    }
                    Spacer(Modifier.height(52.dp))
                    BookingButton("Записаться", Modifier.align(Alignment.CenterHorizontally)
                        .widthIn(max = 468.dp).fillMaxWidth().focusRequester(bookingFocus), wide = true, onClick = onBook)
                } else {
                    UpcomingAppointments()
                    Spacer(Modifier.height(5.dp))
                    BookingButton("Записаться", Modifier.fillMaxWidth().focusRequester(bookingFocus), onClick = onBook)
                    Spacer(Modifier.height(20.dp))
                    MedicalRecords(onOpen)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 880)
@Composable
private fun AppointmentsScreenPreview() {
    ClinicTheme(ThemeChoice.Light) {
        AppointmentsScreen(wide = true, onOpen = {}, bookingFocus = remember { FocusRequester() }, onBook = {})
    }
}

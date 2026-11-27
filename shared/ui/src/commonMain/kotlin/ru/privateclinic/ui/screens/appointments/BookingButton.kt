package ru.privateclinic.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.focus.clinicFocus
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.LocalAccent
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun BookingButton(label: String, modifier: Modifier, wide: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    Box(modifier.clinicFocus(interaction, 14.dp).clip(RoundedCornerShape(14.dp)).background(c.primaryContainer)
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
        .heightIn(min = if (wide) 78.dp else 46.dp)
        .padding(12.dp), contentAlignment = Alignment.Center) {
        Text(label, color = LocalAccent.current.barInk, fontSize = if (wide) 24.sp else 16.sp,
            fontWeight = FontWeight(620))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 46)
@Composable
private fun BookingButtonPreview() {
    ClinicTheme(ThemeChoice.Light) {
        BookingButton("Записаться", Modifier.fillMaxWidth(), onClick = {})
    }
}

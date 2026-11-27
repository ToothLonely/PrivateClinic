package ru.privateclinic.ui.components.row

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.focus.clinicFocus
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun RouteRow(route: ClinicRoute, onOpen: (ClinicRoute) -> Unit, trailing: String? = null) {
    val interaction = remember { MutableInteractionSource() }
    Column(Modifier.fillMaxWidth().clinicFocus(interaction)
        .clickable(interactionSource = interaction, indication = null, role = Role.Button) { onOpen(route) }) {
        Row(Modifier.fillMaxWidth().heightIn(min = 46.dp).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(route.title, fontSize = 16.sp, fontWeight = FontWeight(620))
            if (trailing != null) Text(trailing, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RouteRowPreview() {
    ClinicTheme(ThemeChoice.Light) {
        RouteRow(ClinicRoute.Favorites, onOpen = {}, trailing = "1")
    }
}

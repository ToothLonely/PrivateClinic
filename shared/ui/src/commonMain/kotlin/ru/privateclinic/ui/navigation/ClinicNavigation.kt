package ru.privateclinic.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.focus.clinicFocus
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.LocalAccent
import ru.privateclinic.ui.theme.SemiBold
import ru.privateclinic.ui.theme.ThemeChoice

internal val ClinicTabs = listOf(ClinicRoute.Appointments, ClinicRoute.Catalog, ClinicRoute.Profile)
private val tabIcons = listOf(ClinicIcon.Calendar, ClinicIcon.Clinic, ClinicIcon.User)

@Composable
internal fun ClinicNavigation(wide: Boolean, selected: ClinicRoute, onSelect: (ClinicRoute) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val ink = LocalAccent.current.barInk
    Box(Modifier.fillMaxWidth().padding(
        start = if (wide) 24.dp else 14.dp, end = if (wide) 24.dp else 14.dp,
        top = if (wide) 10.dp else 4.dp, bottom = if (wide) 16.dp else 14.dp,
    ), contentAlignment = Alignment.Center) {
        Row(Modifier.widthIn(max = 480.dp).fillMaxWidth().clip(CircleShape)
            .background(colors.primaryContainer).padding(5.dp).selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            ClinicTabs.forEachIndexed { index, route ->
                val active = route == selected
                val tint = if (active) colors.onSecondaryContainer else ink
                val interaction = remember { MutableInteractionSource() }
                Column(Modifier.weight(1f).clinicFocus(interaction, Dp.Infinity).clip(CircleShape)
                    .then(if (active) Modifier.background(colors.secondaryContainer) else Modifier)
                    .selectable(active, interactionSource = interaction, indication = null,
                        role = Role.Tab, onClick = { onSelect(route) }).heightIn(min = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)) {
                    Symbol(tabIcons[index], tint, 22.dp)
                    Text(route.title, color = tint, fontSize = 12.sp, fontWeight = SemiBold, lineHeight = 17.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 72)
@Composable
private fun ClinicNavigationPreview() {
    ClinicTheme(ThemeChoice.Light) {
        ClinicNavigation(wide = false, selected = ClinicRoute.Catalog, onSelect = {})
    }
}

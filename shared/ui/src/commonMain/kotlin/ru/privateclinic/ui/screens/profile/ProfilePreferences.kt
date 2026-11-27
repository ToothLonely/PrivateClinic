package ru.privateclinic.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.focus.clinicFocus
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.components.layout.SectionTitle
import ru.privateclinic.ui.components.row.RouteRow
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.SemiBold
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun ProfilePreferences(favoriteCount: Int, theme: ThemeChoice, onTheme: (ThemeChoice) -> Unit, onOpen: (ClinicRoute) -> Unit, wide: Boolean, labelSize: TextUnit) {
    RouteRow(ClinicRoute.PersonalData, onOpen)
    RouteRow(ClinicRoute.Favorites, onOpen, trailing = favoriteCount.toString())
    Spacer(Modifier.height(if (wide) 32.dp else 28.dp))
    SectionTitle("Тема")
    Spacer(Modifier.height(if (wide) 16.dp else 12.dp))
    Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeChoice.entries.forEach { choice ->
            val c = MaterialTheme.colorScheme
            val shape = RoundedCornerShape(16.dp)
            val active = choice == theme
            val interaction = remember { MutableInteractionSource() }
            Column(Modifier.weight(1f).clinicFocus(interaction, 16.dp).clip(shape).background(c.surface)
                .border(if (active) 2.5.dp else 1.dp, if (active) c.primary else c.outlineVariant, shape)
                .selectable(active, interactionSource = interaction, indication = null,
                    role = Role.RadioButton, onClick = { onTheme(choice) })
                .padding(horizontal = 7.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                val swatch = RoundedCornerShape(9.dp)
                val light = Color(0xFFFCF8F1)
                val dark = Color(0xFF191E25)
                val fill = when (choice) {
                    ThemeChoice.System -> Modifier.background(Brush.verticalGradient(listOf(light, dark)), swatch)
                    ThemeChoice.Light -> Modifier.background(light, swatch)
                    ThemeChoice.Dark -> Modifier.background(dark, swatch)
                }
                Spacer(Modifier.fillMaxWidth().height(54.dp).then(fill).border(1.dp, Color(0x66838991), swatch))
                Text(choice.label, fontSize = labelSize, lineHeight = (labelSize.value * 1.4f).sp,
                    fontWeight = FontWeight(620), textAlign = TextAlign.Center)
            }
        }
    }
    Spacer(Modifier.height(30.dp))
    val danger = MaterialTheme.colorScheme.error
    Row(Modifier.fillMaxWidth().heightIn(min = 46.dp).border(1.dp, danger, CircleShape)
        .clickable(enabled = false, role = Role.Button) {},
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically) {
        Symbol(ClinicIcon.Logout, danger)
        Text("Выйти", color = danger, fontWeight = SemiBold)
    }
    Spacer(Modifier.height(8.dp))
}

@Preview(showBackground = true, widthDp = 360, heightDp = 342)
@Composable
private fun ProfilePreferencesPreview() {
    ClinicTheme(ThemeChoice.Dark) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfilePreferences(
                favoriteCount = 1, theme = ThemeChoice.Dark, onTheme = {}, onOpen = {},
                wide = false, labelSize = 12.87.sp,
            )
        }
    }
}

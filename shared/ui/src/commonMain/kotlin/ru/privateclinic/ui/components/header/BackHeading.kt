package ru.privateclinic.ui.components.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import ru.privateclinic.ui.components.focus.clinicFocus
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun BackHeading(title: String, progress: Float, wide: Boolean, size: Float, onBack: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            Box(Modifier.widthIn(min = 46.dp, max = 46.dp).heightIn(min = 46.dp)
                .clinicFocus(interaction).clickable(interactionSource = interaction, indication = null,
                    role = Role.Button, onClick = onBack)
                .semantics { contentDescription = "Назад" }, contentAlignment = Alignment.Center) {
                Symbol(ClinicIcon.Back, MaterialTheme.colorScheme.onSurface, 24.dp)
            }
            Text(title, Modifier.semantics { heading() },
                fontSize = size.sp, lineHeight = (size * 1.1f).sp, letterSpacing = (-1.1).sp, fontWeight = FontWeight.Bold)
        },
    ) { measurables, constraints ->
        val button = measurables[0].measure(Constraints(maxWidth = constraints.maxWidth))
        val gap = (if (wide) 14.dp else 8.dp).roundToPx()
        val titleStart = button.width + gap
        val heading = measurables[1].measure(Constraints(maxWidth = (constraints.maxWidth - titleStart).coerceAtLeast(0)))
        val rowHeight = maxOf(button.height, heading.height + 2 * 16.dp.roundToPx())
        val drop = ((heading.height + 10.dp.roundToPx()) * progress).roundToInt()

        layout(constraints.maxWidth, rowHeight + drop) {
            button.placeRelative(0, (rowHeight - button.height) / 2)
            heading.placeRelative((titleStart * (1f - progress)).roundToInt(), (rowHeight - heading.height) / 2 + drop)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun BackHeadingPreview() {
    ClinicTheme(ThemeChoice.Light) {
        BackHeading("Личные данные", progress = 0f, wide = false, size = 31f, onBack = {})
    }
}

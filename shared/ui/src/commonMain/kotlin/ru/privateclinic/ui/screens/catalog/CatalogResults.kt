package ru.privateclinic.ui.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.constrainHeight
import kotlin.math.roundToInt
import ru.privateclinic.ui.components.row.ClinicRow
import ru.privateclinic.ui.platform.isWeb
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewBranch
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun CatalogResults(branches: List<PreviewBranch>, wide: Boolean, columns: Int, modifier: Modifier = Modifier) {
    if (branches.isEmpty()) {
        Text("Каталог пока недоступен", modifier, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    if (isWeb && !wide) {
        Layout(
            content = { branches.forEach { ClinicRow(it) } },
            modifier = modifier.fillMaxWidth().padding(bottom = 10.dp),
        ) { measurables, constraints ->
            val cards = measurables.map { it.measure(constraints.copy(minHeight = 0)) }
            // Distribute the fractional CSS row step across the list instead of rounding each row up.
            val gap = 101.8.dp.toPx() - 92.dp.roundToPx()
            val height = cards.sumOf { it.height } + ((cards.size - 1) * gap).roundToInt()
            layout(constraints.maxWidth, constraints.constrainHeight(height)) {
                var top = 0
                cards.forEachIndexed { index, card ->
                    card.placeRelative(0, top + (index * gap).roundToInt())
                    top += card.height
                }
            }
        }
        return
    }
    val gap = if (wide) 12.dp else 10.dp
    Column(modifier.fillMaxWidth().padding(bottom = if (wide) 0.dp else gap),
        verticalArrangement = Arrangement.spacedBy(gap)) {
        branches.chunked(columns).forEach { group ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                group.forEach { ClinicRow(it, Modifier.weight(1f), wideCatalog = wide) }
                if (group.size < columns) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 880, heightDp = 104)
@Composable
private fun CatalogResultsPreview() {
    ClinicTheme(ThemeChoice.Light) {
        CatalogResults(ClinicPreviewData.branches.take(2), wide = true, columns = 2)
    }
}

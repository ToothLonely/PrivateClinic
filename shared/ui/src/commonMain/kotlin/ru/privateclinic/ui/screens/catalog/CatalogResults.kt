package ru.privateclinic.ui.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.privateclinic.ui.components.row.ClinicRow
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

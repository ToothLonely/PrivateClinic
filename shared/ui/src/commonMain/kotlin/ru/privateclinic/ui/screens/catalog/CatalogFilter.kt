package ru.privateclinic.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.LocalAccent
import ru.privateclinic.ui.theme.SemiBold
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun CatalogFilter(label: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(LocalAccent.current.filter)
        .clickable(enabled = false, role = Role.Button) {}.heightIn(min = 46.dp).padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 15.sp, fontWeight = SemiBold)
        Symbol(ClinicIcon.Down, colors.onSurface, 15.dp)
    }
}

@Preview(showBackground = true, widthDp = 190, heightDp = 46)
@Composable
private fun CatalogFilterPreview() {
    ClinicTheme(ThemeChoice.Light) {
        CatalogFilter("Все типы")
    }
}

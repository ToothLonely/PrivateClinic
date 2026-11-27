package ru.privateclinic.ui.screens.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.privateclinic.ui.components.layout.BackScreen
import ru.privateclinic.ui.components.row.ClinicRow
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewBranch
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun FavoritesScreen(
    favorites: List<PreviewBranch>,
    wide: Boolean,
    limitCardWidth: Boolean,
    onBack: () -> Unit,
    onCatalog: () -> Unit,
) {
    BackScreen(ClinicRoute.Favorites.title, wide, onBack, maxContentWidth = 1280.dp) {
        if (favorites.isEmpty()) {
            Text(
                "Сохраняйте клиники из их карточек.",
                Modifier.padding(top = 10.dp, bottom = 16.dp),
            )
            CatalogLink(onCatalog)
        } else {
            val cardWidth = if (limitCardWidth) 720.dp else Dp.Infinity
            favorites.forEach { branch ->
                ClinicRow(branch, Modifier.widthIn(max = cardWidth).padding(bottom = 10.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 880)
@Composable
private fun FavoritesScreenPreview() {
    ClinicTheme(ThemeChoice.Light) {
        FavoritesScreen(ClinicPreviewData.favorites, wide = true, limitCardWidth = true, onBack = {}, onCatalog = {})
    }
}

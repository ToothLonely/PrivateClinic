package ru.privateclinic.ui.screens.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.sign
import ru.privateclinic.ui.components.header.ClinicHeader
import ru.privateclinic.ui.components.layout.contentInset
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewBranch
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun CatalogScreen(wide: Boolean, search: String, branches: List<PreviewBranch>, onSearch: (String) -> Unit) {
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    val focus = LocalFocusManager.current
    var showSearch by remember(wide) { mutableStateOf(true) }
    LaunchedEffect(scroll, wide, density) {
        if (wide) return@LaunchedEffect
        var previous = scroll.value
        var travel = 0
        val threshold = with(density) { 10.dp.roundToPx() }
        val start = with(density) { 48.dp.roundToPx() }
        snapshotFlow { scroll.value }.collect { y ->
            val delta = y - previous
            previous = y
            travel = if (delta.sign == travel.sign) travel + delta else delta
            if (y < 2 || travel < -threshold) showSearch = true
            if (y > start && travel > threshold && showSearch) {
                focus.clearFocus()
                showSearch = false
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        ClinicHeader(wide) {
            AnimatedVisibility(wide || showSearch, enter = expandVertically(tween(200)) + fadeIn(tween(180)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(180))) {
                Box(Modifier.padding(top = if (wide) 0.dp else 17.dp)) { SearchField(search, onSearch) }
            }
        }
        BoxWithConstraints(Modifier.fillMaxSize().verticalScroll(scroll), contentAlignment = Alignment.TopCenter) {
            val width = maxWidth
            val inset = contentInset(wide, width)
            val gap = (width * .03f).coerceIn(18.dp, 52.dp)
            val bounds = Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(
                start = inset, end = inset, top = inset, bottom = if (wide) inset else 18.dp,
            )
            if (wide) Row(bounds, horizontalArrangement = Arrangement.spacedBy(gap)) {
                val filterWidth = ((width.coerceAtMost(1280.dp) - inset * 2 - gap) * (.65f / 2.65f))
                    .coerceAtLeast(165.dp)
                Column(Modifier.width(filterWidth).zIndex(1f).offset {
                    IntOffset(0, (scroll.value - inset.roundToPx()).coerceAtLeast(0))
                }, verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    CatalogFilter("Все типы")
                    CatalogFilter("Весь город")
                }
                CatalogResults(branches, true, if (width >= 1000.dp) 2 else 1, Modifier.weight(1f))
            } else Column(bounds, verticalArrangement = Arrangement.spacedBy(15.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    CatalogFilter("Все типы", Modifier.weight(1.2f))
                    CatalogFilter("Весь город", Modifier.weight(1f))
                }
                CatalogResults(branches, false, 1)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CatalogScreenPreview() {
    ClinicTheme(ThemeChoice.Light) {
        CatalogScreen(wide = false, search = "", branches = ClinicPreviewData.branches, onSearch = {})
    }
}

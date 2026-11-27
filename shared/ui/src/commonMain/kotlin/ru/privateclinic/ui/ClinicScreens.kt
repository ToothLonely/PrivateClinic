package ru.privateclinic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sign

internal val ClinicTabs = listOf(ClinicRoute.Appointments, ClinicRoute.Catalog, ClinicRoute.Profile)
private val tabIcons = listOf(ClinicIcon.Calendar, ClinicIcon.Clinic, ClinicIcon.User)
internal val SemiBold = FontWeight(650)

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
                Column(Modifier.weight(1f).clip(CircleShape)
                    .then(if (active) Modifier.background(colors.secondaryContainer) else Modifier)
                    .selectable(active, role = Role.Tab, onClick = { onSelect(route) }).heightIn(min = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)) {
                    Symbol(tabIcons[index], tint, 22.dp)
                    Text(route.title, color = tint, fontSize = 12.sp, fontWeight = SemiBold, lineHeight = 17.sp)
                }
            }
        }
    }
}

@Composable
internal fun ClinicHeader(wide: Boolean, search: (@Composable () -> Unit)? = null) {
    val colors = MaterialTheme.colorScheme
    val tint = colors.onPrimaryContainer
    BoxWithConstraints(Modifier.fillMaxWidth().background(colors.primaryContainer,
        RoundedCornerShape(bottomStart = if (wide) 0.dp else 24.dp, bottomEnd = if (wide) 0.dp else 24.dp))) {
        val narrowWide = wide && maxWidth < 800.dp
        val side = if (wide) (maxWidth * .03f).coerceIn(18.dp, 44.dp) else 20.dp
        val centerWidth = ((maxWidth - side * 2) * .38f).coerceAtLeast(240.dp)
        Column(Modifier.padding(start = side, end = side, top = if (wide) 20.dp else 23.dp,
            bottom = if (wide) 20.dp else 16.dp)) {
            val brandSize = if (narrowWide) 24 else if (wide) 30 else 29
            val brandHeight = with(LocalDensity.current) { brandSize.sp.toDp() }
            Box(Modifier.fillMaxWidth().heightIn(min = brandHeight), contentAlignment = Alignment.Center) {
                Row(Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (narrowWide) 8.dp else 12.dp)) {
                    Text("ЧП", modifier = Modifier.height(brandHeight).wrapContentHeight(unbounded = true),
                        color = tint, fontSize = brandSize.sp, lineHeight = brandSize.sp,
                        letterSpacing = (-2).sp, fontWeight = FontWeight.Black)
                    if (wide) Text("Частный приём", color = tint, fontSize = if (narrowWide) 16.sp else 20.sp,
                        fontWeight = SemiBold, letterSpacing = (-.7).sp)
                }
                if (wide && search != null) Box(Modifier.width(centerWidth)) { search() }
                Row(Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Symbol(ClinicIcon.Pin, tint, 14.dp)
                    Text("Ставрополь", color = tint, fontSize = 14.sp, fontWeight = SemiBold)
                }
            }
            if (!wide) search?.invoke()
        }
    }
}

@Composable
private fun SearchField(value: String, onSearch: (String) -> Unit) {
    val palette = LocalAccent.current
    val focus = remember { MutableInteractionSource() }
    val focused by focus.collectIsFocusedAsState()
    val shape = RoundedCornerShape(13.dp)
    val label = "Название или адрес"
    val input = LocalTextStyle.current.copy(color = palette.searchInk, fontSize = 16.sp, lineHeight = 22.sp)
    BasicTextField(value, onSearch, modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
        textStyle = input, singleLine = true, interactionSource = focus, cursorBrush = SolidColor(palette.searchInk),
        decorationBox = { field ->
            Row(Modifier.fillMaxWidth().heightIn(min = 44.dp).background(palette.search, shape)
                .then(if (focused) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape) else Modifier)
                .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Symbol(ClinicIcon.Search, palette.searchInk)
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(label, style = input, color = palette.searchInk.copy(alpha = .87f))
                    field()
                }
            }
        })
}

@Composable
internal fun CatalogScreen(wide: Boolean, search: String, onSearch: (String) -> Unit) {
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
            val inset = contentInset(wide, maxWidth)
            val gap = (maxWidth * .03f).coerceIn(18.dp, 52.dp)
            val bounds = Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(inset)
            if (wide) Row(bounds, horizontalArrangement = Arrangement.spacedBy(gap)) {
                Column(Modifier.weight(.65f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    CatalogFilter("Все типы")
                    CatalogFilter("Весь город")
                }
                Box(Modifier.weight(2f)) { CatalogStatus() }
            } else Column(bounds, verticalArrangement = Arrangement.spacedBy(15.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    CatalogFilter("Все типы", Modifier.weight(1.2f))
                    CatalogFilter("Весь город", Modifier.weight(1f))
                }
                CatalogStatus()
            }
        }
    }
}

@Composable
private fun CatalogStatus() {
    Text("Каталог пока недоступен", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

// Until the catalogue is connected there are no results to filter.
@Composable
private fun CatalogFilter(label: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(LocalAccent.current.filter)
        .clickable(enabled = false, role = Role.Button) {}.heightIn(min = 46.dp).padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 15.sp, fontWeight = SemiBold)
        Symbol(ClinicIcon.Down, colors.onSurface, 15.dp)
    }
}

internal fun contentInset(wide: Boolean, width: Dp) =
    if (wide) (width * .03f).coerceIn(24.dp, 48.dp) else 15.dp

@Composable
internal fun SectionTitle(title: String) {
    Text(title, Modifier.semantics { heading() }, fontSize = 22.sp, lineHeight = 31.sp,
        letterSpacing = (-.3).sp, fontWeight = FontWeight.Bold)
}

@Composable
internal fun RouteRow(route: ClinicRoute, onOpen: (ClinicRoute) -> Unit, trailing: String? = null) {
    Column(Modifier.fillMaxWidth().clickable(role = Role.Button) { onOpen(route) }) {
        Row(Modifier.fillMaxWidth().heightIn(min = 46.dp).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(route.title, fontSize = 16.sp, fontWeight = FontWeight(620))
            if (trailing != null) Text(trailing, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

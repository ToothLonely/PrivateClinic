package ru.privateclinic.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Surface
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics

@Composable
internal fun AppointmentsScreen(wide: Boolean, onOpen: (ClinicRoute) -> Unit, onBook: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ClinicHeader(wide)
        BoxWithConstraints(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
            val inset = contentInset(wide, maxWidth)
            val side = (maxWidth * .03f).coerceIn(18.dp, 42.dp)
            Column(Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(inset)) {
                if (wide) {
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Column(Modifier.weight(1f).padding(end = side)) { Upcoming() }
                        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Column(Modifier.weight(1f).padding(start = side)) { Records(onOpen) }
                    }
                    Spacer(Modifier.height(52.dp))
                    BookingButton("Записаться", Modifier.align(Alignment.CenterHorizontally)
                        .widthIn(max = 468.dp).fillMaxWidth(), wide = true, onClick = onBook)
                } else {
                    Upcoming()
                    Spacer(Modifier.height(5.dp))
                    BookingButton("Записаться", Modifier.fillMaxWidth(), onClick = onBook)
                    Spacer(Modifier.height(20.dp))
                    Records(onOpen)
                }
            }
        }
    }
}

@Composable
internal fun BookingNotice(onDismiss: () -> Unit, onCatalog: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val focus = remember { FocusRequester() }
    Box(Modifier.fillMaxSize().semantics { dialog() }.onPreviewKeyEvent {
        when (it.key) {
            Key.Escape -> {
                if (it.type == KeyEventType.KeyUp) onDismiss()
                true
            }
            Key.Tab -> true // The dialog has one focusable action.
            else -> false
        }
    }, contentAlignment = Alignment.Center) {
        Box(Modifier.fillMaxSize().background(Color(0x80111928)).pointerInput(onDismiss) {
            detectTapGestures { onDismiss() }
        })
        val shape = RoundedCornerShape(22.dp)
        Surface(Modifier.padding(horizontal = 16.dp).widthIn(max = 340.dp).fillMaxWidth(),
            shape = shape, color = colors.surface, shadowElevation = 14.dp,
            border = BorderStroke(1.dp, colors.outlineVariant)) {
            Column(Modifier.padding(21.dp)) {
                Text("Функция сейчас недоступна", color = colors.onSurface, fontSize = 21.sp,
                    lineHeight = 27.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                BookingButton("Перейти в каталог", Modifier.fillMaxWidth().focusRequester(focus), onClick = onCatalog)
            }
        }
    }
    LaunchedEffect(Unit) { focus.requestFocus() }
}

@Composable
private fun BookingButton(label: String, modifier: Modifier, wide: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(c.primaryContainer)
        .clickable(role = Role.Button, onClick = onClick).heightIn(min = if (wide) 78.dp else 46.dp)
        .padding(12.dp), contentAlignment = Alignment.Center) {
        Text(label, color = LocalAccent.current.barInk, fontSize = if (wide) 24.sp else 16.sp,
            fontWeight = FontWeight(620))
    }
}

@Composable
private fun Upcoming() {
    Spacer(Modifier.height(10.dp))
    SectionTitle("Текущие записи")
    Spacer(Modifier.height(10.dp))
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(15.dp)
    Box(Modifier.fillMaxWidth().heightIn(min = 144.dp).background(c.surfaceVariant, shape)
        .border(1.dp, c.outlineVariant, shape), contentAlignment = Alignment.Center) {
        Text("У вас нет записей", color = c.onSurfaceVariant)
    }
}

@Composable
private fun Records(onOpen: (ClinicRoute) -> Unit) {
    Spacer(Modifier.height(10.dp))
    SectionTitle("Медкарта")
    Spacer(Modifier.height(10.dp))
    listOf(ClinicRoute.Visits, ClinicRoute.Studies, ClinicRoute.LabResults).forEach { RouteRow(it, onOpen) }
}

@Composable
internal fun ProfileScreen(wide: Boolean, theme: ThemeChoice, onTheme: (ThemeChoice) -> Unit, onOpen: (ClinicRoute) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ClinicHeader(wide)
        BoxWithConstraints(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
            val inset = contentInset(wide, maxWidth)
            val gap = (maxWidth * .05f).coerceIn(24.dp, 84.dp)
            val side = (maxWidth * .03f).coerceIn(18.dp, 42.dp)
            val labelSize = (maxWidth.value * .033f).coerceIn(12f, 15f).sp
            val frame = Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(inset)
            val c = MaterialTheme.colorScheme
            // Identity is populated by the authenticated profile feature.
            if (wide) {
                Row(frame.height(IntrinsicSize.Min)) {
                    Spacer(Modifier.weight(1.15f))
                    Spacer(Modifier.width(gap))
                    VerticalDivider(color = c.outlineVariant)
                    Column(Modifier.weight(1f).padding(start = side)) { Preferences(theme, onTheme, onOpen, wide, labelSize) }
                }
            } else {
                Column(frame) { Preferences(theme, onTheme, onOpen, wide, labelSize) }
            }
        }
    }
}

@Composable
private fun Preferences(theme: ThemeChoice, onTheme: (ThemeChoice) -> Unit, onOpen: (ClinicRoute) -> Unit, wide: Boolean, labelSize: TextUnit) {
    RouteRow(ClinicRoute.PersonalData, onOpen)
    RouteRow(ClinicRoute.Favorites, onOpen)
    Spacer(Modifier.height(if (wide) 32.dp else 28.dp))
    SectionTitle("Тема")
    Spacer(Modifier.height(if (wide) 16.dp else 12.dp))
    Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeChoice.entries.forEach { choice ->
            val c = MaterialTheme.colorScheme
            val shape = RoundedCornerShape(16.dp)
            val active = choice == theme
            Column(Modifier.weight(1f).clip(shape).background(c.surface)
                .border(if (active) 2.5.dp else 1.dp, if (active) c.primary else c.outlineVariant, shape)
                .selectable(active, role = Role.RadioButton, onClick = { onTheme(choice) })
                .padding(horizontal = 6.dp, vertical = 8.dp),
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
                Text(choice.label, fontSize = labelSize, fontWeight = FontWeight(620), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
internal fun PersonalDataLabels() {
    Column(Modifier.padding(top = 20.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(23.dp)) {
        listOf("Фамилия, имя, отчество", "Электронная почта").forEach { label ->
            Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun CatalogLink(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(14.dp))
        .background(LocalAccent.current.filter).clickable(role = Role.Button, onClick = onClick)
        .heightIn(min = 46.dp).padding(12.dp), contentAlignment = Alignment.Center) {
        Text("К клиникам", color = colors.onSurface, fontWeight = FontWeight(620))
    }
}

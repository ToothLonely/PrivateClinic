package ru.privateclinic.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
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
                        Row(Modifier.weight(1f).height(IntrinsicSize.Min)) {
                            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Column(Modifier.weight(1f).padding(start = side)) { Records(onOpen) }
                        }
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
        Surface(Modifier.padding(horizontal = 16.dp).widthIn(max = 340.dp).fillMaxWidth()
            .dropShadow(shape, Shadow(radius = 22.5.dp, color = Color(0x44000000), offset = DpOffset(0.dp, 14.dp))),
            shape = shape, color = colors.surface,
            border = BorderStroke(1.dp, colors.outlineVariant)) {
            Column(Modifier.padding(22.dp)) {
                Text("Функция сейчас недоступна", color = colors.onSurface, fontSize = 21.sp,
                    lineHeight = 27.3.sp, letterSpacing = (-.3).sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                BookingButton("Перейти в каталог", Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    .focusRequester(focus), onClick = onCatalog)
            }
        }
    }
    LaunchedEffect(Unit) { focus.requestFocus() }
}

@Composable
private fun BookingButton(label: String, modifier: Modifier, wide: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    Box(modifier.clinicFocus(interaction, 14.dp).clip(RoundedCornerShape(14.dp)).background(c.primaryContainer)
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
        .heightIn(min = if (wide) 78.dp else 46.dp)
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
internal fun ProfileScreen(
    wide: Boolean,
    profile: PreviewProfile,
    favoriteCount: Int,
    theme: ThemeChoice,
    onTheme: (ThemeChoice) -> Unit,
    onOpen: (ClinicRoute) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ClinicHeader(wide)
        BoxWithConstraints(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
            val width = maxWidth
            val inset = contentInset(wide, width)
            val gap = (width * .05f).coerceIn(24.dp, 84.dp)
            val side = (width * .03f).coerceIn(18.dp, 42.dp)
            val labelSize = (width.value * .033f).coerceIn(12f, 15f).sp
            val frame = Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(inset)
            val c = MaterialTheme.colorScheme
            if (wide) {
                Row(frame.height(IntrinsicSize.Min)) {
                    ProfileIdentity(profile, width, wide = true, modifier = Modifier.weight(1.15f))
                    Spacer(Modifier.width(gap))
                    Row(Modifier.weight(1f).height(IntrinsicSize.Min)) {
                        VerticalDivider(color = c.outlineVariant)
                        Column(Modifier.weight(1f).padding(start = side)) {
                            Preferences(favoriteCount, theme, onTheme, onOpen, wide, labelSize)
                        }
                    }
                }
            } else {
                Column(frame) {
                    ProfileIdentity(profile, width, wide = false)
                    HorizontalDivider(color = c.outlineVariant)
                    Preferences(favoriteCount, theme, onTheme, onOpen, wide, labelSize)
                }
            }
        }
    }
}

@Composable
private fun ProfileIdentity(profile: PreviewProfile, width: Dp, wide: Boolean, modifier: Modifier = Modifier) {
    val avatar = if (wide) (width * .08f).coerceIn(80.dp, 110.dp) else 54.dp
    val nameSize = if (wide) (width.value * .032f).coerceIn(28f, 42f) else 23f
    Row(modifier.padding(top = if (wide) 4.dp else 12.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(if (wide) 20.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(avatar).background(LocalAccent.current.disc, CircleShape).clearAndSetSemantics {},
            contentAlignment = Alignment.Center) {
            val size = if (wide) 32 else 19
            Text(profile.initials, fontSize = size.sp, lineHeight = (size * 1.4f).sp, fontWeight = FontWeight.Bold)
        }
        Text(profile.firstName, Modifier.semantics { heading() }, fontSize = nameSize.sp,
            lineHeight = (nameSize * 1.2f).sp, letterSpacing = (-1.1).sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Preferences(favoriteCount: Int, theme: ThemeChoice, onTheme: (ThemeChoice) -> Unit, onOpen: (ClinicRoute) -> Unit, wide: Boolean, labelSize: TextUnit) {
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

@Composable
internal fun PersonalData(profile: PreviewProfile) {
    Column(Modifier.padding(top = 20.dp, bottom = 24.dp)) {
        listOf("Фамилия, имя, отчество" to profile.fullName, "Электронная почта" to profile.email).forEach { (label, value) ->
            Text(label, fontSize = 14.sp, lineHeight = 19.6.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, Modifier.padding(top = 5.dp, bottom = 18.dp))
        }
    }
}

@Composable
internal fun CatalogLink(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    Box(Modifier.fillMaxWidth().padding(vertical = 5.dp).clinicFocus(interaction, 14.dp).clip(RoundedCornerShape(14.dp))
        .background(LocalAccent.current.filter)
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
        .heightIn(min = 46.dp).padding(12.dp), contentAlignment = Alignment.Center) {
        Text("К клиникам", color = colors.onSurface, fontWeight = FontWeight(620))
    }
}

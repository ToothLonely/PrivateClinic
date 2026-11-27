package ru.privateclinic.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Navy = Color(0xFF10274C)
private val Sky = Color(0xFF86D2FA)

private val LightColors = lightColorScheme(
    primary = Color(0xFF113967),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF66C7FB),
    onPrimaryContainer = Color(0xFF091C34),
    secondaryContainer = Navy,
    onSecondaryContainer = Color.White,
    background = Color(0xFFFCF8F1),
    onBackground = Color(0xFF0D203C),
    surface = Color(0xFFFFFDF9),
    onSurface = Color(0xFF0D203C),
    surfaceVariant = Color(0xFFE8F4FC),
    onSurfaceVariant = Color(0xFF49586A),
    outline = Color(0xFF49586A),
    outlineVariant = Color(0xFFCDDCE6),
    error = Color(0xFFB73142),
)

private val DarkColors = darkColorScheme(
    primary = Sky,
    onPrimary = Navy,
    primaryContainer = Color(0xFF124489),
    onPrimaryContainer = Color.White,
    secondaryContainer = Sky,
    onSecondaryContainer = Navy,
    background = Color(0xFF191E25),
    onBackground = Color(0xFFF6F8FA),
    surface = Color(0xFF232C36),
    onSurface = Color(0xFFF6F8FA),
    surfaceVariant = Color(0xFF303A46),
    onSurfaceVariant = Color(0xFFC6D2DF),
    outline = Color(0xFFC6D2DF),
    outlineVariant = Color(0xFF455363),
    error = Color(0xFFFF8994),
)

private val ClinicShapes = Shapes(
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

internal val WideWindow = 680.dp

internal enum class ThemeChoice(val label: String) {
    System("Системная"), Light("Светлая"), Dark("Тёмная")
}

internal data class AccentColors(
    val filter: Color,
    val disc: Color,
    val barInk: Color,
) {
    val search = Color(0xFFFFFEFE)
    val searchInk = Color(0xFF34445B)
}

private val LightAccent = AccentColors(Color(0xFFDFF0FC), Color(0xFFC4E7FC), Navy)
private val DarkAccent = AccentColors(Color(0xFF3B4654), Color(0xFF496784), Color.White)
internal val LocalAccent = staticCompositionLocalOf { LightAccent }

@Composable
internal fun Modifier.clinicFocus(source: InteractionSource, radius: Dp = 0.dp): Modifier {
    val focused by source.collectIsFocusedAsState()
    if (!focused || LocalInputModeManager.current.inputMode != InputMode.Keyboard) return this
    return drawBehind {
        val stroke = 3.dp.toPx()
        val outset = 2.dp.toPx() + stroke / 2f
        val corner = if (radius > 0.dp) minOf(radius.toPx(), size.minDimension / 2) + outset else 0f
        drawRoundRect(
            color = Color(0xFF497FCC),
            topLeft = Offset(-outset, -outset),
            size = Size(size.width + 2 * outset, size.height + 2 * outset),
            cornerRadius = CornerRadius(corner),
            style = Stroke(stroke),
        )
    }
}

@Composable
internal fun ClinicTheme(choice: ThemeChoice, fontFamily: FontFamily, content: @Composable () -> Unit) {
    val dark = when (choice) {
        ThemeChoice.System -> isSystemInDarkTheme()
        ThemeChoice.Light -> false
        ThemeChoice.Dark -> true
    }
    CompositionLocalProvider(LocalAccent provides if (dark) DarkAccent else LightAccent) {
        MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, shapes = ClinicShapes) {
            val textStyle = TextStyle(fontFamily = fontFamily, fontSize = 16.sp, lineHeight = 22.4.sp, letterSpacing = 0.sp,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None))
            CompositionLocalProvider(LocalTextStyle provides textStyle) {
                content()
            }
        }
    }
}

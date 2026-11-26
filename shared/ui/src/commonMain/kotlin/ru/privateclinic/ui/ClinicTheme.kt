package ru.privateclinic.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

@Composable
internal fun ClinicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = ClinicShapes,
        content = content,
    )
}

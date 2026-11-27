package ru.privateclinic.ui.components.focus

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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

internal fun Modifier.clinicFocus(source: InteractionSource, radius: Dp = 0.dp): Modifier = composed {
    val focused by source.collectIsFocusedAsState()
    if (!focused || LocalInputModeManager.current.inputMode != InputMode.Keyboard) return@composed Modifier
    Modifier.drawBehind {
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

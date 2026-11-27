package ru.privateclinic.ui.components.layout

import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val WideWindow = 680.dp

internal fun contentInset(wide: Boolean, width: Dp): Dp =
    if (wide) (width * .03f).coerceIn(24.dp, 48.dp) else 15.dp

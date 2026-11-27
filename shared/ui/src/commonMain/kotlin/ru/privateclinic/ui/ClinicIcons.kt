package ru.privateclinic.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// The paths use the same 24-unit coordinate system as the approved prototype.
internal enum class ClinicIcon(pathData: String) {
    Clinic("M3 21V9h5V4h8v5h5v12H3Zm7 0v-4h4v4M10.5 8h3m-1.5-1.5v3M6 13v1m12-1v1"),
    Tooth("M12 5c-4-3-9-2-9 3 0 3 2 6 3 10 .9 4 3 4 4-1 .6-3 3.4-3 4 0 1 5 3 5 4 1 1-4 3-7 3-10 0-5-5-6-9-3Z"),
    Calendar("M6 5h12a3 3 0 0 1 3 3v10a3 3 0 0 1 -3 3H6a3 3 0 0 1 -3 -3V8a3 3 0 0 1 3 -3ZM7 3v5m10-5v5M3 11h18"),
    User("M8 7a4 4 0 1 0 8 0a4 4 0 1 0 -8 0ZM4 21v-2c0-7 16-7 16 0v2Z"),
    Pin("M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 1 1 16 0ZM9.4 10a2.6 2.6 0 1 0 5.2 0a2.6 2.6 0 1 0 -5.2 0Z"),
    Search("M4 10.5a6.5 6.5 0 1 0 13 0a6.5 6.5 0 1 0 -13 0ZM16 16l5 5"),
    Down("m6 9 6 6 6-6"),
    Back("M20 12H4m7-7-7 7 7 7"),
    Logout("M10 4H5v16h5m-1-8h12m-5-5 5 5-5 5");

    val path = PathParser().parsePathString(pathData).toPath()
}

@Composable
internal fun Symbol(icon: ClinicIcon, color: Color, size: Dp = 20.dp) {
    Canvas(Modifier.size(size)) {
        scale(this.size.width / 24f, pivot = Offset.Zero) {
            drawPath(icon.path, color, style = Stroke(1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

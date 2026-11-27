package ru.privateclinic.ui.components.icon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun Symbol(icon: ClinicIcon, color: Color, size: Dp = 20.dp) {
    Canvas(Modifier.size(size)) {
        scale(this.size.width / 24f, pivot = Offset.Zero) {
            drawPath(icon.path, color, style = Stroke(1.75f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

@Preview(showBackground = true, widthDp = 100, heightDp = 60)
@Composable
private fun SymbolPreview() {
    ClinicTheme(ThemeChoice.Light) {
        Symbol(ClinicIcon.Clinic, MaterialTheme.colorScheme.onSurface, 27.dp)
    }
}

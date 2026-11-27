package ru.privateclinic.ui.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.screens.catalog.SearchField
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.SemiBold
import ru.privateclinic.ui.theme.ThemeChoice

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

@Preview(showBackground = true, widthDp = 390, heightDp = 129)
@Composable
private fun ClinicHeaderPreview() {
    ClinicTheme(ThemeChoice.Light) {
        ClinicHeader(wide = false) { SearchField(value = "", onSearch = {}) }
    }
}

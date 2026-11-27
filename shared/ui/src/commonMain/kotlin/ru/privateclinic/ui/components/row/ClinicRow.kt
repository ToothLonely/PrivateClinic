package ru.privateclinic.ui.components.row

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.icon.ClinicIcon
import ru.privateclinic.ui.components.icon.Symbol
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewBranch
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.LocalAccent
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun ClinicRow(branch: PreviewBranch, modifier: Modifier = Modifier, wideCatalog: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    val palette = LocalAccent.current
    val icon = if (branch.profile == "Стоматология") ClinicIcon.Tooth else ClinicIcon.Clinic
    Row(
        modifier.fillMaxWidth().background(colors.surfaceVariant, RoundedCornerShape(19.dp))
            .heightIn(min = if (wideCatalog) 104.dp else 92.dp)
            .padding(horizontal = if (wideCatalog) 16.dp else 12.dp, vertical = if (wideCatalog) 16.dp else 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(48.dp).background(palette.disc, CircleShape), contentAlignment = Alignment.Center) {
            Symbol(icon, colors.onSurface, 27.dp)
        }
        Column(Modifier.weight(1f, fill = false)
            .align(if (wideCatalog) Alignment.CenterVertically else Alignment.Top)) {
            Text(branch.name, fontSize = 18.sp, lineHeight = 25.2.sp, letterSpacing = (-.5).sp,
                fontWeight = FontWeight.Bold)
            Text(branch.profile, color = colors.onSurfaceVariant, fontSize = 13.sp, lineHeight = 18.2.sp)
            Row(Modifier.heightIn(min = 22.4.dp).padding(top = 1.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.height(18.2.dp), contentAlignment = Alignment.Center) {
                    Symbol(ClinicIcon.Pin, colors.onSurface, 14.dp)
                }
                Text(branch.address, fontSize = 13.sp, lineHeight = 18.2.sp)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 92)
@Composable
private fun ClinicRowPreview() {
    ClinicTheme(ThemeChoice.Light) {
        ClinicRow(ClinicPreviewData.branches.first())
    }
}

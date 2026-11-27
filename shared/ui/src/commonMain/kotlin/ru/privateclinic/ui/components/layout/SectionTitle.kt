package ru.privateclinic.ui.components.layout

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun SectionTitle(title: String) {
    Text(title, Modifier.semantics { heading() }, fontSize = 22.sp, lineHeight = 31.sp,
        letterSpacing = (-.3).sp, fontWeight = FontWeight.Bold)
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SectionTitlePreview() {
    ClinicTheme(ThemeChoice.Light) {
        SectionTitle("Медкарта")
    }
}

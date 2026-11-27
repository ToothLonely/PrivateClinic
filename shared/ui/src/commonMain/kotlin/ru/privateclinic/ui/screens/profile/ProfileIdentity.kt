package ru.privateclinic.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewProfile
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.LocalAccent
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun ProfileIdentity(profile: PreviewProfile, width: Dp, wide: Boolean, modifier: Modifier = Modifier) {
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

@Preview(showBackground = true, widthDp = 360, heightDp = 87)
@Composable
private fun ProfileIdentityPreview() {
    ClinicTheme(ThemeChoice.Light) {
        ProfileIdentity(ClinicPreviewData.profile, width = 390.dp, wide = false)
    }
}

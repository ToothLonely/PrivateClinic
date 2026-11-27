package ru.privateclinic.ui.screens.profile

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.components.header.ClinicHeader
import ru.privateclinic.ui.components.layout.contentInset
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.state.ClinicPreviewData
import ru.privateclinic.ui.state.PreviewProfile
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun ProfileScreen(
    wide: Boolean,
    profile: PreviewProfile,
    favoriteCount: Int,
    theme: ThemeChoice,
    onTheme: (ThemeChoice) -> Unit,
    onOpen: (ClinicRoute) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ClinicHeader(wide)
        BoxWithConstraints(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
            val width = maxWidth
            val inset = contentInset(wide, width)
            val gap = (width * .05f).coerceIn(24.dp, 84.dp)
            val side = (width * .03f).coerceIn(18.dp, 42.dp)
            val labelSize = (width.value * .033f).coerceIn(12f, 15f).sp
            val frame = Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(inset)
            val c = MaterialTheme.colorScheme
            if (wide) {
                Row(frame.height(IntrinsicSize.Min)) {
                    ProfileIdentity(profile, width, wide = true, modifier = Modifier.weight(1.15f))
                    Spacer(Modifier.width(gap))
                    Row(Modifier.weight(1f).height(IntrinsicSize.Min)) {
                        VerticalDivider(color = c.outlineVariant)
                        Column(Modifier.weight(1f).padding(start = side)) {
                            ProfilePreferences(favoriteCount, theme, onTheme, onOpen, wide, labelSize)
                        }
                    }
                }
            } else {
                Column(frame) {
                    ProfileIdentity(profile, width, wide = false)
                    HorizontalDivider(color = c.outlineVariant)
                    ProfilePreferences(favoriteCount, theme, onTheme, onOpen, wide, labelSize)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileScreenPreview() {
    ClinicTheme(ThemeChoice.Dark) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileScreen(
                wide = false, profile = ClinicPreviewData.profile, favoriteCount = ClinicPreviewData.favorites.size,
                theme = ThemeChoice.Dark, onTheme = {}, onOpen = {},
            )
        }
    }
}

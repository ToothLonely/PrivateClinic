package ru.privateclinic.ui.screens.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.privateclinic.ui.platform.isWeb
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun BookingNotice(onDismiss: () -> Unit, onCatalog: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val focus = remember { FocusRequester() }
    Box(Modifier.fillMaxSize().semantics { dialog() }.onPreviewKeyEvent {
        when (it.key) {
            Key.Escape -> {
                if (it.type == KeyEventType.KeyUp) onDismiss()
                true
            }
            Key.Tab -> true // The dialog has one focusable action.
            else -> false
        }
    }, contentAlignment = Alignment.Center) {
        Box(Modifier.fillMaxSize().background(Color(0x80111928)).pointerInput(onDismiss) {
            detectTapGestures { onDismiss() }
        })
        val shape = RoundedCornerShape(22.dp)
        // CSS blur 45 uses sigma 22.5. Compose on Wasm converts its radius to sigma.
        val shadowRadius = if (isWeb) ((45f / 2 - .5f) / .57735f).dp else 22.5.dp
        Surface(Modifier.padding(horizontal = 16.dp).widthIn(max = 340.dp).fillMaxWidth()
            .dropShadow(shape, Shadow(radius = shadowRadius, color = Color(0x44000000), offset = DpOffset(0.dp, 14.dp))),
            shape = shape, color = colors.surface,
            border = BorderStroke(1.dp, colors.outlineVariant)) {
            Column(Modifier.padding(22.dp)) {
                Text("Функция сейчас недоступна", color = colors.onSurface, fontSize = 21.sp,
                    lineHeight = 27.3.sp, letterSpacing = (-.3).sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                BookingButton("Перейти в каталог", Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    .focusRequester(focus), onClick = onCatalog)
            }
        }
    }
    LaunchedEffect(Unit) { focus.requestFocus() }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun BookingNoticePreview() {
    ClinicTheme(ThemeChoice.Light) {
        BookingNotice(onDismiss = {}, onCatalog = {})
    }
}

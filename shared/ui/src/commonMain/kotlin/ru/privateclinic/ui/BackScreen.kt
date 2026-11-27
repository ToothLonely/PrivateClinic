package ru.privateclinic.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

internal class HeaderPull(private val distance: Float, wide: Boolean) {
    private val headerTravel = if (wide) 0f else 80f
    val titleProgress = if (wide) 0f else (distance / headerTravel).coerceIn(0f, 1f)
    val showsRefresh = distance > headerTravel
    val canRefresh = distance >= headerTravel + 50f
}

@Composable
internal fun BackScreen(
    title: String,
    wide: Boolean,
    onBack: () -> Unit,
    onRefresh: (() -> Unit)? = null,
    maxContentWidth: Dp = Dp.Infinity,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    val refresh by rememberUpdatedState(onRefresh)
    var distance by remember(wide) { mutableFloatStateOf(0f) }
    val pull = HeaderPull(distance, wide)

    val gesture = if (!wide || onRefresh != null) {
        Modifier.pointerInput(wide, density) {
            awaitEachGesture {
                var down: PointerInputChange?
                do {
                    down = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull {
                        it.type == PointerType.Touch && it.changedToDownIgnoreConsumed()
                    }
                } while (down == null)
                if (scroll.canScrollBackward) return@awaitEachGesture

                var pulling = false
                try {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val touch = event.changes.firstOrNull { it.id == down.id } ?: break
                        val anotherTouch = event.changes.any {
                            it.id != down.id && it.type == PointerType.Touch && it.pressed
                        }
                        if (touch.isConsumed || anotherTouch) break
                        if (!touch.pressed) {
                            if (pulling && HeaderPull(distance, wide).canRefresh) refresh?.invoke()
                            break
                        }

                        val movement = touch.position - down.position
                        if (movement.y <= 0f || abs(movement.x) > movement.y) {
                            if (pulling || maxOf(abs(movement.x), abs(movement.y)) > viewConfiguration.touchSlop) break
                            continue
                        }
                        if (!pulling) {
                            if (scroll.canScrollBackward) break
                            if (movement.y <= viewConfiguration.touchSlop) continue
                            pulling = true
                        }
                        touch.consume()
                        distance = with(density) { movement.y.toDp().value.coerceAtLeast(0f) }
                    }
                } finally {
                    distance = 0f
                }
            }
        }
    } else {
        Modifier
    }

    BoxWithConstraints(Modifier.fillMaxSize().then(gesture), contentAlignment = Alignment.TopCenter) {
        val inset = contentInset(wide, maxWidth)
        val minimumHeight = maxHeight
        val headingSize = if (wide) (maxWidth.value * .03f).coerceIn(26f, 42f) else 31f
        Column(
            Modifier.widthIn(max = maxContentWidth).fillMaxSize().verticalScroll(scroll).heightIn(min = minimumHeight)
                .padding(start = inset, end = inset, top = inset, bottom = if (wide) inset else 18.dp),
        ) {
            BackHeading(title, pull.titleProgress, wide, headingSize, onBack)
            if (pull.showsRefresh) {
                Text(
                    text = if (pull.canRefresh) "Отпустите для обновления" else "Потяните для обновления",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp),
                    fontSize = 14.sp, lineHeight = 19.6.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}

@Composable
private fun BackHeading(title: String, progress: Float, wide: Boolean, size: Float, onBack: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            Box(Modifier.widthIn(min = 46.dp, max = 46.dp).heightIn(min = 46.dp)
                .clinicFocus(interaction).clickable(interactionSource = interaction, indication = null,
                    role = Role.Button, onClick = onBack)
                .semantics { contentDescription = "Назад" }, contentAlignment = Alignment.Center) {
                Symbol(ClinicIcon.Back, MaterialTheme.colorScheme.onSurface, 24.dp)
            }
            Text(title, Modifier.semantics { heading() },
                fontSize = size.sp, lineHeight = (size * 1.1f).sp, letterSpacing = (-1.1).sp, fontWeight = FontWeight.Bold)
        },
    ) { measurables, constraints ->
        val button = measurables[0].measure(Constraints(maxWidth = constraints.maxWidth))
        val gap = (if (wide) 14.dp else 8.dp).roundToPx()
        val titleStart = button.width + gap
        val heading = measurables[1].measure(Constraints(maxWidth = (constraints.maxWidth - titleStart).coerceAtLeast(0)))
        val rowHeight = maxOf(button.height, heading.height + 2 * 16.dp.roundToPx())
        val drop = ((heading.height + 10.dp.roundToPx()) * progress).roundToInt()

        layout(constraints.maxWidth, rowHeight + drop) {
            button.placeRelative(0, (rowHeight - button.height) / 2)
            heading.placeRelative((titleStart * (1f - progress)).roundToInt(), (rowHeight - heading.height) / 2 + drop)
        }
    }
}

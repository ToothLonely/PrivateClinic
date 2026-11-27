package ru.privateclinic.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
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
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    val refresh by rememberUpdatedState(onRefresh)
    var distance by remember(wide) { mutableFloatStateOf(0f) }
    val visibleDistance = key(wide) {
        animateFloatAsState(distance, animationSpec = if (distance > 0f) snap() else tween(160)).value
    }
    val pull = HeaderPull(visibleDistance, wide)

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
                        if (!pulling) {
                            if (abs(movement.x) > viewConfiguration.touchSlop && abs(movement.x) > movement.y) break
                            if (movement.y < -viewConfiguration.touchSlop || scroll.canScrollBackward) break
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

    BoxWithConstraints(Modifier.fillMaxSize().then(gesture)) {
        val inset = contentInset(wide, maxWidth)
        val minimumHeight = maxHeight
        val headingSize = if (wide) (maxWidth.value * .03f).coerceIn(26f, 42f) else 31f
        Column(
            Modifier.fillMaxSize().verticalScroll(scroll).heightIn(min = minimumHeight).padding(inset),
        ) {
            BackHeading(title, pull.titleProgress, wide, headingSize, onBack)
            if (pull.showsRefresh) {
                Text(
                    text = if (pull.canRefresh) "Отпустите для обновления" else "Потяните для обновления",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}

@Composable
private fun BackHeading(title: String, progress: Float, wide: Boolean, size: Float, onBack: () -> Unit) {
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            IconButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "Назад" }) {
                Symbol(ClinicIcon.Back, MaterialTheme.colorScheme.onSurface, 24.dp)
            }
            Text(title, Modifier.semantics { heading() }.padding(vertical = 16.dp),
                fontSize = size.sp, lineHeight = (size * 1.1f).sp, letterSpacing = (-1.1).sp, fontWeight = FontWeight.Bold)
        },
    ) { measurables, constraints ->
        val button = measurables[0].measure(Constraints(maxWidth = constraints.maxWidth))
        val gap = (if (wide) 14.dp else 8.dp).roundToPx()
        val titleX = ((button.width + gap) * (1f - progress)).roundToInt()
        val heading = measurables[1].measure(Constraints(maxWidth = (constraints.maxWidth - titleX).coerceAtLeast(0)))
        val rowHeight = maxOf(button.height, heading.height)
        val inlineY = (rowHeight - heading.height) / 2
        val titleY = inlineY + ((rowHeight + gap - inlineY) * progress).roundToInt()

        layout(constraints.maxWidth, maxOf(rowHeight, titleY + heading.height)) {
            button.placeRelative(0, (rowHeight - button.height) / 2)
            heading.placeRelative(titleX, titleY)
        }
    }
}

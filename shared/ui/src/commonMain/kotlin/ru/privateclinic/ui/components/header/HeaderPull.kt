package ru.privateclinic.ui.components.header

internal class HeaderPull(private val distance: Float, wide: Boolean) {
    private val headerTravel = if (wide) 0f else 80f
    val titleProgress = if (wide) 0f else (distance / headerTravel).coerceIn(0f, 1f)
    val showsRefresh = distance > headerTravel
    val canRefresh = distance >= headerTravel + 50f
}

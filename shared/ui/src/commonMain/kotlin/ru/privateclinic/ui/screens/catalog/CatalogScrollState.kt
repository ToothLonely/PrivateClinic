package ru.privateclinic.ui.screens.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateOf

internal class CatalogScrollState {
    val scroll = ScrollState(0)
    val showSearch = mutableStateOf(true)
}

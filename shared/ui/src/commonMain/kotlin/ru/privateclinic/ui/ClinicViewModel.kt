package ru.privateclinic.ui

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal enum class ClinicRoute(val title: String) : NavKey {
    Catalog("Клиники"),
    Appointments("Запись"),
    Profile("Профиль"),
    PersonalData("Личные данные"),
    Favorites("Избранное"),
    Visits("История приёмов"),
    Studies("Исследования"),
    LabResults("Анализы"),
}

internal data class ClinicUiState(
    val backStack: List<ClinicRoute> = listOf(ClinicRoute.Catalog),
    val search: String = "",
)

internal class ClinicViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(ClinicUiState())
    val state = mutableState.asStateFlow()

    fun selectTab(tab: ClinicRoute) {
        val stack = if (tab == ClinicRoute.Catalog) listOf(tab) else listOf(ClinicRoute.Catalog, tab)
        mutableState.update { it.copy(backStack = stack) }
    }

    fun open(route: ClinicRoute) {
        mutableState.update {
            if (route in it.backStack) it else it.copy(backStack = it.backStack + route)
        }
    }

    fun back() {
        mutableState.update {
            if (it.backStack.size > 1) it.copy(backStack = it.backStack.dropLast(1)) else it
        }
    }

    fun search(value: String) {
        mutableState.update { it.copy(search = value) }
    }
}

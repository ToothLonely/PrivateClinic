package ru.privateclinic.ui.navigation

import androidx.navigation3.runtime.NavKey
import ru.privateclinic.ui.screens.profile.PersonalData

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

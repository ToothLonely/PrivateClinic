package ru.privateclinic.ui.state

internal data class PreviewBranch(val name: String, val profile: String, val address: String)

internal data class PreviewProfile(val firstName: String, val lastName: String, val patronymic: String, val email: String) {
    val initials = "${firstName.first()}${lastName.first()}"
    val fullName = "$lastName $firstName $patronymic"
}

// Local fixtures from the approved prototype. Account and catalogue features will supply live data.
internal object ClinicPreviewData {
    val profile = PreviewProfile("Анна", "Соколова", "Андреевна", "anna@example.com")

    private val stomion = PreviewBranch("Стомион", "Стоматология", "45-я Параллель, 22Г")
    val branches = listOf(
        PreviewBranch("Феникс Медика", "Многопрофильная клиника", "50 лет ВЛКСМ, 91"),
        PreviewBranch("DL Med", "Многопрофильная клиника", "50 лет ВЛКСМ, 18В"),
        stomion,
        PreviewBranch("Сантерра", "Медицинский центр", "Серова, 154"),
        PreviewBranch("Тринити-Дент", "Стоматология", "Шпаковская, 76/1"),
        PreviewBranch("Клиника Семейная", "Многопрофильная клиника", "Серова, 252"),
        PreviewBranch("Никольс.Мед", "Медицинский центр", "Мира, 126"),
        PreviewBranch("Айвазовский", "Стоматология", "Рогожникова, 3"),
        PreviewBranch("Доктор плюс", "Медицинский центр", "Ленина, 127"),
        PreviewBranch("Хорошая стоматология", "Стоматология", "Мира, 242"),
        PreviewBranch("Медстудио", "Стоматология", "Партизанская, 2Б"),
        PreviewBranch("Амадея", "Медицинский центр", "45-я Параллель, 2"),
    )
    val favorites = listOf(stomion)
}

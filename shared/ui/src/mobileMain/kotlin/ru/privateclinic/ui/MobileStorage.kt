package ru.privateclinic.ui

import org.koin.dsl.module
import org.koin.dsl.onClose
import ru.privateclinic.storage.ClinicDatabase

internal fun mobileStorageModule(createDatabase: () -> ClinicDatabase) = module {
    single { createDatabase() } onClose { it?.close() }
    single { LocalStorage(get<ClinicDatabase>()::open) }
}

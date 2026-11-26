package ru.privateclinic.storage

import androidx.room.Room
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun createClinicDatabase(): ClinicDatabase {
    val directory = checkNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )?.path,
    ) { "Could not open Application Support" }
    return Room.databaseBuilder<ClinicDatabase>(name = "$directory/$DatabaseName").buildClinicDatabase()
}

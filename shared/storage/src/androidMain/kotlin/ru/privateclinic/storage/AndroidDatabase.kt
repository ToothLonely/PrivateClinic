package ru.privateclinic.storage

import android.content.Context
import androidx.room.Room

fun createClinicDatabase(context: Context): ClinicDatabase {
    val application = context.applicationContext
    return Room.databaseBuilder<ClinicDatabase>(application, DatabaseName).buildClinicDatabase()
}

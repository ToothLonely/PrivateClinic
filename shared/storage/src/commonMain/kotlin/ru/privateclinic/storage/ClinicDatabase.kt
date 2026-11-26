package ru.privateclinic.storage

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Upsert
import androidx.room.useReaderConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

@Entity(tableName = "client_settings")
data class ClientSetting(@PrimaryKey val key: String, val value: String)

@Dao
interface ClientSettingsDao {
    @Query("SELECT value FROM client_settings WHERE `key` = :key")
    suspend fun read(key: String): String?

    @Upsert
    suspend fun write(setting: ClientSetting)
}

@Database(entities = [ClientSetting::class], version = 1)
@ConstructedBy(ClinicDatabaseConstructor::class)
abstract class ClinicDatabase : RoomDatabase() {
    abstract fun clientSettings(): ClientSettingsDao

    suspend fun open() {
        useReaderConnection { connection ->
            connection.usePrepared("SELECT 1") { it.step() }
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT") // Room generates the actual on each mobile target.
expect object ClinicDatabaseConstructor : RoomDatabaseConstructor<ClinicDatabase> {
    override fun initialize(): ClinicDatabase
}

internal const val DatabaseName = "clinic.db"

internal fun RoomDatabase.Builder<ClinicDatabase>.buildClinicDatabase(): ClinicDatabase =
    setDriver(BundledSQLiteDriver()).build()

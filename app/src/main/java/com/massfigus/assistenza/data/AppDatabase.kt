package com.massfigus.assistenza.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.massfigus.assistenza.model.Cliente
import com.massfigus.assistenza.model.Intervento
import com.massfigus.assistenza.model.Macchina

@Database(
    entities = [Cliente::class, Macchina::class, Intervento::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun macchinaDao(): MacchinaDao
    abstract fun interventoDao(): InterventoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "assistenza.db"
                ).build().also { INSTANCE = it }
            }
    }
}

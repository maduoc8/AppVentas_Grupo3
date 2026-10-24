package com.example.appventas.model.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Base aparte para las ventas: así UsuarioDatabase queda igual
@Database(entities = [VentaEntity::class], version = 1)
abstract class VentaDatabase : RoomDatabase() {

    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var INSTANCE: VentaDatabase? = null

        fun getDatabase(context: Context): VentaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    VentaDatabase::class.java,
                    "venta_database"
                ).build().also { INSTANCE = it }
            }
        } // fin getDatabase
    }
}

package com.factupro.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Client::class, User::class, Invoice::class],
    version = 3,
    exportSchema = false
)
abstract class FactuProDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun userDao(): UserDao
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        @Volatile
        private var INSTANCE: FactuProDatabase? = null

        fun getDatabase(context: Context): FactuProDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FactuProDatabase::class.java,
                    "factupro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
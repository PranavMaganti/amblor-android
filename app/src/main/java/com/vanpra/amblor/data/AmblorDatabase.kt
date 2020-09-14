package com.vanpra.amblor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Scrobble::class, Track::class], version = 1, exportSchema = false)
abstract class AmblorDatabase : RoomDatabase() {
    abstract fun scrobbleDao(): ScrobbleDao

    companion object {
        @Volatile
        private var INSTANCE: AmblorDatabase? = null

        fun getDatabase(context: Context): AmblorDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmblorDatabase::class.java,
                    "scrobble_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

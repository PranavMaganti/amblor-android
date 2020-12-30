package com.vanpra.amblor.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Scrobble::class, Track::class], version = 1, exportSchema = false)
abstract class AmblorDatabase : RoomDatabase() {
    abstract fun scrobbleDao(): ScrobbleDao
    abstract fun statsDao(): StatsDao
}

package com.vanpra.amblor.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vanpra.amblor.data.Scrobble
import com.vanpra.amblor.data.ScrobbleDao
import com.vanpra.amblor.data.StatsDao
import com.vanpra.amblor.data.Track

@Database(entities = [Scrobble::class, Track::class], version = 2, exportSchema = false)
abstract class AmblorDatabase : RoomDatabase() {
    abstract fun scrobbleDao(): ScrobbleDao
    abstract fun statsDao(): StatsDao
}
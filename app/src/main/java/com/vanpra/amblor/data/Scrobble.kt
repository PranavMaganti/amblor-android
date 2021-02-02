package com.vanpra.amblor.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Track::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("track_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Scrobble(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Int,
    val track_id: Long
)


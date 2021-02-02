package com.vanpra.amblor.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id")])
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val preview_url: String,
    val album_name: String,
    val image: String,
    val artist_names: String,
    val artist_images: String,
    val artist_genres: String
)

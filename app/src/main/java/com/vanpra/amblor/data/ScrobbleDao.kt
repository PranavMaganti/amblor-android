package com.vanpra.amblor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vanpra.amblor.models.ScrobbleData
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrobbleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrobble(scrobble: Scrobble)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track): Long

    @Query("SELECT * FROM Track WHERE name= :trackName AND artist_names=:artistNames")
    suspend fun getTrackByName(trackName: String, artistNames: String): Track?

    @Query(
        "SELECT time, name, preview_url, album_name, image, artist_names, artist_images, " +
            "artist_genres FROM Scrobble JOIN Track ON track_id=Track.id " +
            "ORDER BY Scrobble.time DESC"
    )
    fun getAllScrobbles(): Flow<List<ScrobbleData>>

    @Query("SELECT time FROM Scrobble ORDER BY time DESC")
    suspend fun getLastScrobbleTime(): Int?

    suspend fun insertAllScrobbles(rawScrobbles: List<ScrobbleData>) {
        rawScrobbles.forEach {
            println("inserting")
            val track = getTrackByName(it.name, it.artist_names)
            val trackId = track?.id
                ?: insertTrack(
                    Track(
                        name = it.name,
                        preview_url = it.preview_url,
                        album_name = it.album_name,
                        image = it.image,
                        artist_names = it.artist_names,
                        artist_images = it.artist_images,
                        artist_genres = it.artist_genres
                    )
                )
            insertScrobble(Scrobble(time = it.time, track_id = trackId))
        }
    }
}

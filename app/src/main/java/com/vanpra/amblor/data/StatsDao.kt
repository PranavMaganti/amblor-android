package com.vanpra.amblor.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class TrackStats(
    val name: String,
    val artist_names: String,
    val image: String,
    val count: Int
)

data class ScrobblesByDay(
    val day: Int,
    val count: Int
)

data class ScrobblesByHour(
    val hour: Int,
    val count: Int
)

@Dao
interface StatsDao {
    @Query("SELECT count(*) from Scrobble WHERE time >= :fromTimestamp")
    fun countAllScrobbles(fromTimestamp: Int = 0): Flow<Int>

    @Query(
        "SELECT strftime(\"%w\", time, 'unixepoch', 'localtime') AS day, " +
            "count(strftime(\"%w\", time, 'unixepoch', 'localtime')) AS count FROM scrobble WHERE " +
            "time >= :fromTimestamp GROUP BY day"
    )
    fun countScrobblesByDay(fromTimestamp: Int = 0): Flow<ScrobblesByDay>

    @Query(
        "SELECT strftime(\"%H\", time, 'unixepoch', 'localtime') AS hour, count(*) AS count " +
            "FROM Scrobble WHERE time >= :fromTimestamp GROUP BY hour"
    )
    fun countScrobblesByHour(fromTimestamp: Int = 0): Flow<List<ScrobblesByHour>>

    @Query(
        "SELECT name, artist_names, image, count(*) as count FROM Scrobble " +
            "JOIN Track ON track_id=Track.id WHERE time >= :fromTimestamp GROUP BY name " +
            "ORDER BY count desc"
    )
    fun countTrackPlays(fromTimestamp: Int = 0): Flow<List<TrackStats>>
}

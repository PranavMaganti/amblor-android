package com.vanpra.amblor.ui.layouts.scrobble

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.models.ScrobbleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ScrobbleViewState(
    val scrobbles: List<ScrobbleData> = emptyList(),
    val selectedScrobble: ScrobbleData = ScrobbleData(),
    val showingScrobble: Boolean = false,
    val refreshing: Boolean = false
)

data class Artist(
    val name: String,
    val image: String,
    val genres: List<String>
) {
    companion object {
        fun from(scrobble: ScrobbleData): List<Artist> {
            val names = scrobble.artist_names.split("/")
            val images = scrobble.artist_images.split(" ")
            val genresStr = scrobble.artist_names.split("/")

            return names.mapIndexed { index, name ->
                val image = images[index]
                val genres = genresStr[index].split(",")

                Artist(name, image, genres)
            }
        }
    }
}

class ScrobbleViewModel(application: Application) : AndroidViewModel(application) {
    private val scrobbleDao =
        AmblorDatabase.getDatabase(application.applicationContext).scrobbleDao()

    private val refreshing = MutableStateFlow(false)
    private val selectedScrobble = MutableStateFlow(ScrobbleData())
    private val showingScrobble = MutableStateFlow(false)
    val state = MutableStateFlow(ScrobbleViewState())

    init {
        viewModelScope.launch {
            combine(
                scrobbleDao.getAllScrobbles(),
                selectedScrobble,
                showingScrobble,
                refreshing
            ) { scrobbles, selectedScrobble, showingScrobble, refreshing ->
                ScrobbleViewState(scrobbles, selectedScrobble, showingScrobble, refreshing)
            }.catch { throwable ->
                throw throwable
            }.collect {
                state.value = it
            }
        }
    }

    fun openScrobbleView(scrobble: ScrobbleData) {
        selectedScrobble.value = scrobble
        showingScrobble.value = true
    }

    fun closeScrobbleView() {
        showingScrobble.value = false
    }
}

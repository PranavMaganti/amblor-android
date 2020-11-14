package com.vanpra.amblor.ui.layouts.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.ui.layouts.scrobble.ScrobbleViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class StatsNavigationState {
    Day,
    Week,
    Month,
    Year,
    AllTime
}
   
data class StatsViewState(
    val navigationState: StatsNavigationState = StatsNavigationState.Day,
    val refreshing: Boolean = false
)

class StatsViewModel(application: Application): AndroidViewModel(application) {
    private val statsDao = AmblorDatabase.getDatabase(application.applicationContext).statsDao()

    private val navState = MutableStateFlow(StatsNavigationState.Day)
    private val refreshing = MutableStateFlow(false)

    val viewState = MutableStateFlow(StatsViewState())

    init {
        viewModelScope.launch {
            combine(navState, refreshing) { navState, refreshing ->
                StatsViewState(navState, refreshing)
            }.catch { throwable ->
                throw throwable
            }.collect {
                viewState.value = it
            }
        }
    }
}
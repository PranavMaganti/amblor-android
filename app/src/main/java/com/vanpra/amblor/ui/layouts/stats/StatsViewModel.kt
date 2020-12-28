package com.vanpra.amblor.ui.layouts.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel

// enum class StatsNavigationState {
//     Day,
//     Week,
//     Month,
//     Year,
//     AllTime
// }
//
// data class StatsViewState(
//     val navigationState: StatsNavigationState = StatsNavigationState.Day,
//     val refreshing: Boolean = false
// )

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    // private val statsDao = AmblorDatabase.getDatabase(application.applicationContext).statsDao()
    //
    // private val navState = MutableStateFlow(StatsNavigationState.Day)
    // private val refreshing = MutableStateFlow(false)
    //
    // val viewState = MutableStateFlow(StatsViewState())
    //
    // init {
    //     viewModelScope.launch {
    //         combine(navState, refreshing) { navState, refreshing ->
    //             StatsViewState(navState, refreshing)
    //         }.catch { throwable ->
    //             throw throwable
    //         }.collect {
    //             viewState.value = it
    //         }
    //     }
    // }
}

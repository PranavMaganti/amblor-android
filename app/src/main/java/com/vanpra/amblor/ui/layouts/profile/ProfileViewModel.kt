package com.vanpra.amblor.ui.layouts.profile

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.vanpra.amblor.data.AmblorDatabase
import com.vanpra.amblor.interfaces.AuthenticationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileViewModel(
    application: Application,
    private val auth: AuthenticationApi,
    private val db: AmblorDatabase,
    private val preferences: SharedPreferences
) : AndroidViewModel(application) {
    suspend fun signOut() {
        auth.signOut()
        withContext(Dispatchers.IO) {
            db.clearAllTables()
            preferences.edit().clear().apply()
        }
    }
}

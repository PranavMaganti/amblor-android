package com.vanpra.amblor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.vanpra.amblor.interfaces.AuthenticationApi

class MainViewModel(application: Application, private val auth: AuthenticationApi) :
    AndroidViewModel(application)

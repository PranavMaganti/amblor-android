package com.vanpra.amblor.auth

import com.vanpra.amblor.ui.layouts.auth.EmailSignupModel

interface AmblorUserApi {
    suspend fun signupEmailUser(signupModel: EmailSignupModel, idToken: String): Boolean
}

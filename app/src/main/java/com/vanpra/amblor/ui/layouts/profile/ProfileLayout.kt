package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import com.vanpra.amblor.AuthAmbient

@Composable
fun ProfileLayout() {
    val authRepo = AuthAmbient.current
    Button(onClick = { authRepo.googleSignOut() }) {
        Text("Sign Out")
    }
}

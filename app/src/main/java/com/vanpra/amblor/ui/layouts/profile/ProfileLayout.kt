package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.vanpra.amblor.ui.layouts.auth.AuthViewModel

@Composable
fun ProfileLayout(authViewModel: AuthViewModel) {
    Button(onClick = { authViewModel.signOut(clearDB = true) }) {
        Text("Sign Out")
    }
}

package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.vanpra.amblor.ui.layouts.auth.AuthViewModel

@Composable
fun ProfileLayout(mainNavHostController: NavHostController, authViewModel: AuthViewModel) {
    Button(onClick = { authViewModel.signOut(mainNavHostController) }) {
        Text("Sign Out")
    }
}

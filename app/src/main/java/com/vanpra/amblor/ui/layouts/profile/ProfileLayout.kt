package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileLayout(profileViewModel: ProfileViewModel, navigateToLogin: () -> Unit) {
    val coroutineContext = rememberCoroutineScope()
    Button(
        onClick = {
            coroutineContext.launch {
                profileViewModel.signOut()
                navigateToLogin()
            }
        }
    ) {
        Text("Sign Out")
    }
}

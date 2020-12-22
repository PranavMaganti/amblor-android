package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileLayout() {
    Button(onClick = {
        // Signout from auth
    }) {
        Text("Sign Out")
    }
}

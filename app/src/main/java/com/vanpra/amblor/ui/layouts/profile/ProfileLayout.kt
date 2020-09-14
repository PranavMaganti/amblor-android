package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.foundation.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import com.vanpra.amblor.ViewModelAmbient

@Composable
fun ProfileLayout() {
    val viewModel = ViewModelAmbient.current
    TextButton(onClick = { viewModel.googleSignOut() }) {
        Text("Sign Out")
    }
}

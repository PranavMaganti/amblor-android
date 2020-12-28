package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavHostController
import com.vanpra.amblor.MainViewModel

@Composable
fun ProfileLayout(mainNavHostController: NavHostController) {
    val mainViewModel = viewModel<MainViewModel>()
    Button(onClick = { mainViewModel.signOut(mainNavHostController) }) {
        Text("Sign Out")
    }
}

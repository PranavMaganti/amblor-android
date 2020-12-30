package com.vanpra.amblor.ui.layouts.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.vanpra.amblor.MainViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ProfileLayout(mainNavHostController: NavHostController) {
    val mainViewModel = getViewModel<MainViewModel>()
    Button(onClick = { mainViewModel.signOut(mainNavHostController) }) {
        Text("Sign Out")
    }
}
